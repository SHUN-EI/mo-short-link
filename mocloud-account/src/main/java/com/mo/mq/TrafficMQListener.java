package com.mo.mq;

import com.mo.enums.BizCodeEnum;
import com.mo.exception.BizException;
import com.mo.model.EventMessage;
import com.mo.service.TrafficService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2022/3/10
 */
@RabbitListener(queuesToDeclare = {
        @Queue(value = "order.update.traffic.queue"),
        @Queue(value = "${mqconfig.traffic_free_init_queue}")
})
@Component
@Slf4j
public class TrafficMQListener {

    @Autowired
    private TrafficService trafficService;

    @RabbitHandler
    public void processTrafficMessage(EventMessage eventMessage) {
        log.info("监听到消息:TrafficMQListener message消息内容:{}", eventMessage);

        try {

            //处理流量包相关消息
            trafficService.processTrafficMessage(eventMessage);
            log.info("消费成功:{}", eventMessage);

        } catch (Exception e) {
            //处理业务异常，还有进行其他操作，比如记录失败原因
            log.error("消费失败:{}", eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }

    }


}
