package com.mo.mq;

import com.mo.enums.BizCodeEnum;
import com.mo.exception.BizException;
import com.mo.model.EventMessage;
import com.mo.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2022/3/3
 */
@RabbitListener(queues = "${mqconfig.order_close_queue}")
@Component
@Slf4j
public class OrderMQListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void closeOrderHandler(EventMessage eventMessage, Message message, Channel channel) {

        log.info("监听到消息:OrderMQListener message消息内容:{}", message);

        try {

            //关闭订单
            orderService.closeOrder(eventMessage);
            log.info("消费成功:{}", eventMessage);

        } catch (Exception e) {
            //处理业务异常，还有进行其他操作，比如记录失败原因
            log.error("消费失败:{}", eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
    }
}
