package com.mo.mq;

import com.mo.model.EventMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by mo on 2022/2/23
 */
@Component
@Slf4j
@RabbitListener(queuesToDeclare = {@Queue("short_link.error.queue")})
public class ShortLinkErrorMQListener {

    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.error("告警：监听到消息ShortLinkErrorMQListener eventMessage消息内容:{}",eventMessage);
        log.error("告警：Message:{}",message);
        log.error("告警成功，发送通知短信");

    }

}
