package com.mo.service.impl;

import com.mo.enums.EventMessageTypeEnum;
import com.mo.manager.TrafficManager;
import com.mo.model.EventMessage;
import com.mo.model.TrafficDO;
import com.mo.service.TrafficService;
import com.mo.utils.JsonUtil;
import com.mo.vo.ProductVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * Created by mo on 2022/3/10
 */
@Service
@Slf4j
public class TrafficServiceImpl implements TrafficService {

    @Autowired
    private TrafficManager trafficManager;

    /**
     * 处理 MQ队列里面的流量包相关消息
     *
     * @param eventMessage
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void processTrafficMessage(EventMessage eventMessage) {

        String messageType = eventMessage.getEventMessageType();

        if (EventMessageTypeEnum.ORDER_PAY.name().equalsIgnoreCase(messageType)) {
            //订单已支付,新增发放流量包
            String content = eventMessage.getContent();
            Map<String, Object> orderInfoMap = JsonUtil.json2Obj(content, Map.class);

            //还原订单商品信息
            Long accountNo = (Long) orderInfoMap.get("accountNo");
            String outTradeNo = (String) orderInfoMap.get("outTradeNo");
            Integer buyNum = (Integer) orderInfoMap.get("buyNum");

            //商品快照,转换为商品对象
            String productStr = (String) orderInfoMap.get("product");
            ProductVO productVO = JsonUtil.json2Obj(productStr, ProductVO.class);
            log.info("商品信息:{}", productVO);

            //流量包有效期
            LocalDateTime expiredDateTime = LocalDateTime.now().plusDays(productVO.getValidDay());
            Date date = Date.from(expiredDateTime.atZone(ZoneId.systemDefault()).toInstant());

            TrafficDO trafficDO = TrafficDO.builder()
                    .accountNo(accountNo)
                    //每天限制多少条，短链，可叠加
                    .dayLimit(productVO.getDayTimes() * buyNum)
                    .dayUsed(0)
                    .totalLimit(productVO.getTotalTimes())
                    .pluginType(productVO.getPluginType())
                    .level(productVO.getLevel())
                    .productId(productVO.getId())
                    .outTradeNo(outTradeNo)
                    .expiredDate(date)
                    .build();

            //保存
            Integer rows = trafficManager.add(trafficDO);
            log.info("消费消息新增流量包:rows={},trafficDO={}",rows,trafficDO);
        }
    }
}
