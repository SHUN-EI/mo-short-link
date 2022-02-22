package com.mo.mq;

import com.mo.model.EventMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2022/2/23
 */
@Component
@RabbitListener(queues = "${mqconfig.short_link_add_mapping_queue}")
@Slf4j
public class ShortLinkAddMappingMQListener {

    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) {

        log.info("监听到消息:ShortLinkAddMappingMQListener message消息内容:{}", message);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            //TODO 处理业务逻辑

            log.info("消费成功:{}",eventMessage);
            //确认消息消费成功
            channel.basicAck(deliveryTag,false);

        } catch (Exception e) {
            //处理业务异常，还有进行其他操作，比如记录失败原因
            log.error("消费失败:{}",eventMessage);
        }
    }


}
