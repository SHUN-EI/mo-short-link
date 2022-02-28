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
     * 新增短链——link队列
     */
    private String shortLinkAddLinkQueue;

    /**
     * 新增短链——mapping队列-B端使用
     */
    private String shortLinkAddMappingQueue;

    /**
     * 新增短链——具体的routingKey,【发送消息使用】
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
     * 删除短链——link队列
     */
    private String shortLinkDeleteLinkQueue;

    /**
     * 删除短链——mapping队列-B端使用
     */
    private String shortLinkDeleteMappingQueue;

    /**
     * 删除短链——具体的routingKey,【发送消息使用】
     */
    private String shortLinkDeleteRoutingKey;

    /**
     * topic类型的binding key，用于绑定队列和交换机，是用于 link 消费者
     */
    private String shortLinkDeleteLinkBindingKey;

    /**
     * topic类型的binding key，用于绑定队列和交换机，是用于 mapping 消费者
     */
    private String shortLinkDeleteMappingBindingKey;

    /**
     * 更新短链——link队列
     */
    private String shortLinkUpdateLinkQueue;

    /**
     * 更新短链——mapping队列-B端使用
     */
    private String shortLinkUpdateMappingQueue;

    /**
     * 更新短链——具体的routingKey,【发送消息使用】
     */
    private String shortLinkUpdateRoutingKey;

    /**
     * topic类型的binding key，用于绑定队列和交换机，是用于 link 消费者
     */
    private String shortLinkUpdateLinkBindingKey;

    /**
     * topic类型的binding key，用于绑定队列和交换机，是用于 mapping 消费者
     */
    private String shortLinkUpdateMappingBindingKey;


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
     * 新增短链——link普通队列，用于被监听
     *
     * @return
     */
    @Bean
    public Queue shortLinkAddLinkQueue() {
        return new Queue(shortLinkAddLinkQueue, true, false, false);

    }

    /**
     * 新增短链——mapping 普通队列，用于被监听
     *
     * @return
     */
    @Bean
    public Queue shortLinkAddMappingQueue() {
        return new Queue(shortLinkAddMappingQueue, true, false, false);

    }

    /**
     * 新增短链——link队列和交换机的绑定关系建立,用于 link 消费者
     *
     * @return
     */
    @Bean
    public Binding shortLinkAddApiBinding() {
        return new Binding(shortLinkAddLinkQueue, Binding.DestinationType.QUEUE, shortLinkEventExchange, shortLinkAddLinkBindingKey, null);
    }

    /**
     * 新增短链——mapping队列和交换机的绑定关系建立,是用于 mapping 消费者
     *
     * @return
     */
    @Bean
    public Binding shortLinkAddMappingBinding() {
        return new Binding(shortLinkAddMappingQueue, Binding.DestinationType.QUEUE, shortLinkEventExchange, shortLinkAddMappingBindingKey, null);
    }

    /**
     * 删除短链——link 普通队列，用于被监听
     *
     * @return
     */
    @Bean
    public Queue shortLinkDeleteLinkQueue() {
        return new Queue(shortLinkDeleteLinkQueue, true, false, false);

    }

    /**
     * 删除短链——mapping 普通队列，用于被监听
     *
     * @return
     */
    @Bean
    public Queue shortLinkDeleteMappingQueue() {
        return new Queue(shortLinkDeleteMappingQueue, true, false, false);

    }

    /**
     * 删除短链——link队列和交换机的绑定关系建立,用于 link 消费者
     *
     * @return
     */
    @Bean
    public Binding shortLinkDeleteApiBinding() {
        return new Binding(shortLinkDeleteLinkQueue, Binding.DestinationType.QUEUE, shortLinkEventExchange, shortLinkDeleteLinkBindingKey, null);
    }

    /**
     * 删除短链——mapping队列和交换机的绑定关系建立,是用于 mapping 消费者
     *
     * @return
     */
    @Bean
    public Binding shortLinkDeleteMappingBinding() {
        return new Binding(shortLinkDeleteMappingQueue, Binding.DestinationType.QUEUE, shortLinkEventExchange, shortLinkDeleteMappingBindingKey, null);
    }

    /**
     * 更新短链——link 普通队列，用于被监听
     *
     * @return
     */
    @Bean
    public Queue shortLinkUpdateLinkQueue() {
        return new Queue(shortLinkUpdateLinkQueue, true, false, false);

    }

    /**
     * 更新短链——mapping 普通队列，用于被监听
     *
     * @return
     */
    @Bean
    public Queue shortLinkUpdateMappingQueue() {
        return new Queue(shortLinkUpdateMappingQueue, true, false, false);

    }

    /**
     * 更新短链——link队列和交换机的绑定关系建立,用于 link 消费者
     *
     * @return
     */
    @Bean
    public Binding shortLinkUpdateApiBinding() {
        return new Binding(shortLinkUpdateLinkQueue, Binding.DestinationType.QUEUE, shortLinkEventExchange, shortLinkUpdateLinkBindingKey, null);
    }

    /**
     * 更新短链——mapping队列和交换机的绑定关系建立,是用于 mapping 消费者
     *
     * @return
     */
    @Bean
    public Binding shortLinkUpdateMappingBinding() {
        return new Binding(shortLinkUpdateMappingQueue, Binding.DestinationType.QUEUE, shortLinkEventExchange, shortLinkUpdateMappingBindingKey, null);
    }

}
