package com.mo.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2022/2/23
 */
@ConfigurationProperties(prefix = "mqerrorconfig")
@Configuration
@Data
public class RabbitMQErrorConfig {

    /**
     * 异常交换机
     */
    private String trafficErrorExchange;
    /**
     * 异常消息队列
     */
    private String trafficErrorQueue;
    /**
     * 异常消息的routingKey
     */
    private String trafficErrorRoutingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 创建交换机 Topic类型
     *
     * @return
     */
    @Bean
    public TopicExchange errorTopicExchange() {
        return new TopicExchange(trafficErrorExchange, true, false);
    }

    /**
     * 创建异常队列
     *
     * @return
     */
    @Bean
    public Queue errorQueue() {
        return new Queue(trafficErrorQueue, true, false, false);
    }

    /**
     * 异常队列和交换机的绑定关系建立
     *
     * @return
     */
    @Bean
    public Binding ErrorQueueAndExchangeBinding() {
        return new Binding(trafficErrorQueue, Binding.DestinationType.QUEUE, trafficErrorExchange, trafficErrorRoutingKey, null);
    }

    /**
     * 配置  RepublishMessageRecoverer
     * 消费消息重试一定次数后，用特定的routingKey转发到指定的交换机中，方便后续排查和告警
     */
    @Bean
    public MessageRecoverer messageRecoverer() {
        return new RepublishMessageRecoverer(rabbitTemplate, trafficErrorExchange, trafficErrorRoutingKey);
    }

}
