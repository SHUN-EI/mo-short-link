package com.mo.service.impl;

import com.mo.config.RabbitMQConfig;
import com.mo.enums.DomainTypeEnum;
import com.mo.enums.EventMessageTypeEnum;
import com.mo.enums.ShortLinkStateEnum;
import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.DomainManager;
import com.mo.manager.LinkGroupManager;
import com.mo.manager.ShortLinkManager;
import com.mo.model.*;
import com.mo.request.ShortLinkAddRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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


    /**
     * 处理新增短链消息
     *
     * @param eventMessage
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void handlerAddShortLink(EventMessage eventMessage) {

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

        //TODO 加锁

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
        String shortLinkCode = ShardingDBConfig.getRandomDBPrefix() + code + ShardingTableConfig.getRandomTableSuffix();

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
