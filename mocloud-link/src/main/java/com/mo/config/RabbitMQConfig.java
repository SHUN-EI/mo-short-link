package com.mo.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2022/2/23
 */
@ConfigurationProperties(prefix = "mqconfig")
@Configuration
@Data
public class RabbitMQConfig {

    /**
     * 短链交换机
     */
    private String shortLinkEventExchange;

    /**
     * 新增短链 队列
     */
    private String shortLinkAddLinkQueue;

    /**
     * 新增短链映射队列-B端使用
     */
    private String shortLinkAddMappingQueue;

    /**
     * 新增短链具体的routingKey,【发送消息使用】
     */
    private String shortLinkAddRoutingKey;

    /**
     * topic类型的binding key，用于绑定队列和交换机，是用于 link 消费者
     */
    private String shortLinkAddLinkBindingKey;

    /**
     * topic类型的binding key，用于绑定队列和交换机，是用于 mapping 消费者
     */
    private String shortLinkAddMappingBindingKey;


    /**
     * 创建交换机 Topic类型
     * 一个微服务一个交换机
     *
     * @return
     */
    @Bean
    public Exchange shortLinkEventExchange() {
        return new TopicExchange(shortLinkEventExchange, true, false);
    }

    /**
     * 新增短链api 普通队列，用于被监听
     *
     * @return
     */
    @Bean
    public Queue shortLinkAddLinkQueue() {
        return new Queue(shortLinkAddLinkQueue, true, false, false);

    }

    /**
     * 新增短链mapping 普通队列，用于被监听
     *
     * @return
     */
    @Bean
    public Queue shortLinkAddMappingQueue() {
        return new Queue(shortLinkAddMappingQueue, true, false, false);

    }

    /**
     * 新增短链api队列和交换机的绑定关系建立,用于 link 消费者
     *
     * @return
     */
    @Bean
    public Binding shortLinkAddApiBinding() {
        return new Binding(shortLinkAddLinkQueue, Binding.DestinationType.QUEUE, shortLinkEventExchange, shortLinkAddLinkBindingKey, null);
    }

    /**
     * 新增短链mapping队列和交换机的绑定关系建立,是用于 mapping 消费者
     *
     * @return
     */
    @Bean
    public Binding shortLinkAddMappingBinding() {
        return new Binding(shortLinkAddMappingQueue, Binding.DestinationType.QUEUE, shortLinkEventExchange, shortLinkAddMappingBindingKey, null);
    }

}
