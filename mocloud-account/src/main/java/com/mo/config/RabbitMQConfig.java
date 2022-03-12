package com.mo.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2022/3/10
 */
@ConfigurationProperties(prefix = "mqconfig")
@Configuration
@Data
public class RabbitMQConfig {

    /**
     * 交换机
     */
    private String trafficEventExchange;

    /**
     * 免费流量包新增 队列
     */
    private String trafficFreeInitQueue;

    /**
     * 免费流量包新增 队列路由key
     */
    private String trafficFreeInitRoutingKey;


    /**
     * 创建交换机 Topic类型
     * 一个微服务一个交换机
     * @return
     */
    @Bean
    public Exchange trafficEventExchange(){
        return new TopicExchange(trafficEventExchange,true,false);
    }


    /**
     * 免费流量包新增队列
     * @return
     */
    @Bean
    public Queue trafficFreeInitQueue(){
        return new Queue(trafficFreeInitQueue,true,false,false);
    }

    /**
     * 免费流量包新增队列 与交换机的绑定关系建立
     * @return
     */
    @Bean
    public Binding trafficFreeInitQueueBinding(){
        return new Binding(trafficFreeInitQueue, Binding.DestinationType.QUEUE,trafficEventExchange,trafficFreeInitRoutingKey,null);
    }


    /**
     * 消息转换器，把消息转为json格式
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
