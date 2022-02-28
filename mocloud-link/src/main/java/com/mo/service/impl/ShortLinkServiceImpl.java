package com.mo.service.impl;

import com.mo.config.RabbitMQConfig;
import com.mo.enums.DomainTypeEnum;
import com.mo.enums.EventMessageTypeEnum;
import com.mo.enums.ShortLinkStateEnum;
import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.DomainManager;
import com.mo.manager.GroupCodeMappingManager;
import com.mo.manager.LinkGroupManager;
import com.mo.manager.ShortLinkManager;
import com.mo.model.*;
import com.mo.request.ShortLinkAddRequest;
import com.mo.request.ShortLinkDeleteRequest;
import com.mo.request.ShortLinkPageRequest;
import com.mo.request.ShortLinkUpdateRequest;
import com.mo.service.ShortLinkService;
import com.mo.strategy.ShardingDBConfig;
import com.mo.strategy.ShardingTableConfig;
import com.mo.utils.CommonUtil;
import com.mo.utils.IDUtil;
import com.mo.utils.JsonData;
import com.mo.utils.JsonUtil;
import com.mo.vo.ShortLinkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2022/2/21
 */
@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    private ShortLinkManager shortLinkManager;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    @Autowired
    private DomainManager domainManager;
    @Autowired
    private LinkGroupManager linkGroupManager;
    @Autowired
    private GroupCodeMappingManager groupCodeMappingManager;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    /**
     * 删除短链
     *
     * @param request
     * @return
     */
    @Override
    public JsonData delete(ShortLinkDeleteRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        EventMessage eventMessage = EventMessage.builder().accountNo(loginUserDTO.getAccountNo())
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeID().toString())
                .eventMessageType(EventMessageTypeEnum.SHORT_LINK_DEL.name())
                .build();

        //发送消息
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(), rabbitMQConfig.getShortLinkDeleteRoutingKey(), eventMessage);


        return JsonData.buildSuccess();
    }

    /**
     * 更新短链
     *
     * @param request
     * @return
     */
    @Override
    public JsonData update(ShortLinkUpdateRequest request) {


        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        EventMessage eventMessage = EventMessage.builder().accountNo(loginUserDTO.getAccountNo())
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeID().toString())
                .eventMessageType(EventMessageTypeEnum.SHORT_LINK_UPDATE.name())
                .build();

        //发送消息
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(), rabbitMQConfig.getShortLinkUpdateRoutingKey(), eventMessage);

        return JsonData.buildSuccess();
    }

    /**
     * 分页查找短链-B端
     *
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> pageByGroupId(ShortLinkPageRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        request.setAccountNo(loginUserDTO.getAccountNo());

        Map<String, Object> resultMap = groupCodeMappingManager.pageShortLink(request);

        return resultMap;
    }

    /**
     * 处理新增短链消息
     *
     * @param eventMessage
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Boolean handlerAddShortLink(EventMessage eventMessage) {

        Long accountNo = eventMessage.getAccountNo();
        String eventMessageType = eventMessage.getEventMessageType();

        //消息的消息体——>转换成ShortLinkAddRequest对象
        ShortLinkAddRequest request = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkAddRequest.class);
        //短链域名校验,判断短链域名是否合法
        DomainDO domainDO = checkDomain(request.getDomainType(), request.getDomainId(), accountNo);
        //校验组名是否合法
        LinkGroupDO linkGroupDO = checkLinkGroup(request.getGroupId(), accountNo);
        //生成长链摘要
        String originalUrlDigest = CommonUtil.MD5(request.getOriginalUrl());
        //生成短链码
        String shortLinkCode = createShortLinkCode(request.getOriginalUrl());

        //加锁
        //key1是短链码，ARGV[1]是accountNo,ARGV[2]是过期时间100
        String script = "if redis.call('EXISTS',KEYS[1])==0 then redis.call('set',KEYS[1],ARGV[1]); " +
                "redis.call('expire',KEYS[1],ARGV[2]); return 1;" +
                " elseif redis.call('get',KEYS[1]) == ARGV[1] then return 2;" +
                " else return 0; end;";

        //执行lua脚本
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(shortLinkCode), accountNo, 100);

        //短链码重复标记
        Boolean duplicateCodeFlag = false;

        //加锁成功
        if (result > 0) {
            //C端处理
            if (EventMessageTypeEnum.SHORT_LINK_ADD_LINK.name().equalsIgnoreCase(eventMessageType)) {

                //先判断是否短链码被占用
                ShortLinkDO shortLinCodeDOInDB = shortLinkManager.findByShortLinCode(shortLinkCode);
                if (null == shortLinCodeDOInDB) {

                    ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                            .accountNo(accountNo)
                            .code(shortLinkCode)
                            .title(request.getTitle())
                            .originalUrl(request.getOriginalUrl())
                            .domain(domainDO.getValue())
                            .groupId(linkGroupDO.getId())
                            .expired(request.getExpired())
                            .sign(originalUrlDigest)
                            .state(ShortLinkStateEnum.ACTIVE.name())
                            .del(0)
                            .build();

                    //保存
                    shortLinkManager.addShortLink(shortLinkDO);
                    return true;
                } else {
                    //短链码被占用
                    log.error("C端短链码重复:{}", eventMessage);
                    duplicateCodeFlag = true;
                }
            } else if (EventMessageTypeEnum.SHORT_LINK_ADD_MAPPING.name().equalsIgnoreCase(eventMessageType)) {
                //B端处理
                //先判断是否短链码被占用
                GroupCodeMappingDO groupCodeMappingDOInDB = groupCodeMappingManager.findByCodeAndGroupId(shortLinkCode, linkGroupDO.getId(), accountNo);
                if (null == groupCodeMappingDOInDB) {
                    GroupCodeMappingDO codeMappingDO = GroupCodeMappingDO.builder()
                            .accountNo(accountNo)
                            .code(shortLinkCode)
                            .title(request.getTitle())
                            .originalUrl(request.getOriginalUrl())
                            .domain(domainDO.getValue())
                            .groupId(linkGroupDO.getId())
                            .expired(request.getExpired())
                            .sign(originalUrlDigest)
                            .state(ShortLinkStateEnum.ACTIVE.name())
                            .del(0)
                            .build();

                    //保存
                    groupCodeMappingManager.add(codeMappingDO);
                    return true;
                } else {
                    log.error("B端短链码重复:{}", eventMessage);
                    duplicateCodeFlag = true;
                }

            }
        } else {
            //加锁失败，自旋100毫秒，再调用； 失败的可能是短链码已经被占用，需要重新生成
            log.error("加锁失败:{}", eventMessage);

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
            }

            duplicateCodeFlag = true;
        }

        //短链码重复标记为true
        if (duplicateCodeFlag) {
            //生成新的短链码url
            String newOriginalUrl = CommonUtil.addUrlPrefixVersion(request.getOriginalUrl());
            request.setOriginalUrl(newOriginalUrl);
            eventMessage.setContent(JsonUtil.obj2Json(request));
            log.warn("短链码保存失败，重新生成:{}", eventMessage);
            //递归调用本方法处理
            handlerAddShortLink(eventMessage);
        }
        return false;
    }

    /**
     * 创建短链
     *
     * @param request
     * @return
     */
    @Override
    public JsonData createShortLink(ShortLinkAddRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        //URL增加前缀,保证原始url 能生成唯一的不同的短链码
        //拼接后 1469558440337604610&https://xdclass.net
        String newOriginalUrl = CommonUtil.addUrlPrefix(request.getOriginalUrl());
        request.setOriginalUrl(newOriginalUrl);

        EventMessage eventMessage = EventMessage.builder()
                .accountNo(loginUserDTO.getAccountNo())
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeID().toString())
                .eventMessageType(EventMessageTypeEnum.SHORT_LINK_ADD.name())
                .build();

        //发送消息
        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(), rabbitMQConfig.getShortLinkAddRoutingKey(), eventMessage);

        return JsonData.buildSuccess();
    }

    /**
     * 根据短链码解析短链
     *
     * @param shortLinkCode
     * @return
     */
    @Override
    public ShortLinkVO parseShortLinkCode(String shortLinkCode) {

        ShortLinkDO shortLinkDO = shortLinkManager.findByShortLinCode(shortLinkCode);

        if (null == shortLinkDO) {
            return null;
        }

        ShortLinkVO shortLinkVO = new ShortLinkVO();
        BeanUtils.copyProperties(shortLinkDO, shortLinkVO);

        return shortLinkVO;
    }

    /**
     * 生成短链码
     *
     * @param link
     * @return
     */
    @Override
    public String createShortLinkCode(String link) {

        long murmurHash32 = CommonUtil.murmurHash32(link);
        //进制转换 10进制转62进制
        String code = CommonUtil.encodeToBase62(murmurHash32);

        //短链码:A92AEva1,设计规定第一位为数据库位,最后一位为数据表位
        //String shortLinkCode = ShardingDBConfig.getRandomDBPrefix() + code + ShardingTableConfig.getRandomTableSuffix();

        //通过短链码的hashCode 取模 数据库表数量,得到数据库位和表位,保证长链生成的短链是固定的
        String shortLinkCode = ShardingDBConfig.getRandomDBPrefix(code) + code + ShardingTableConfig.getRandomTableSuffix(code);

        return shortLinkCode;
    }

    /**
     * 校验域名
     *
     * @param domainType
     * @param domainId
     * @param accountNo
     * @return
     */
    private DomainDO checkDomain(String domainType, Long domainId, Long accountNo) {
        DomainDO domainDO = null;

        //用户自建的域名
        if (DomainTypeEnum.CUSTOM.name().equalsIgnoreCase(domainType)) {
            domainDO = domainManager.findById(domainId, accountNo);
        } else {
            //系统提供的域名 OFFICIAL
            domainDO = domainManager.findByDomainTypeAndID(domainId, DomainTypeEnum.OFFICIAL);
        }

        Assert.notNull(domainDO, "短链域名不合法");
        return domainDO;
    }

    /**
     * 校验组名
     *
     * @param groupId
     * @param accountNo
     * @return
     */
    private LinkGroupDO checkLinkGroup(Long groupId, Long accountNo) {
        LinkGroupDO linkGroupDO = linkGroupManager.detail(groupId, accountNo);
        Assert.notNull(linkGroupDO, "组名不合法");
        return linkGroupDO;
    }
}
