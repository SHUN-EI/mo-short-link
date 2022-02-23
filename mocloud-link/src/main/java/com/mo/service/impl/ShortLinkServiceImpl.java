package com.mo.service.impl;

import com.mo.config.RabbitMQConfig;
import com.mo.enums.EventMessageTypeEnum;
import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.ShortLinkManager;
import com.mo.model.EventMessage;
import com.mo.model.LoginUserDTO;
import com.mo.model.ShortLinkDO;
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
}
