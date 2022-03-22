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

import java.util.HashMap;
import java.util.Map;

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
     *
     * @return
     */
    @Bean
    public Exchange trafficEventExchange() {
        return new TopicExchange(trafficEventExchange, true, false);
    }


    /**
     * 免费流量包新增队列
     *
     * @return
     */
    @Bean
    public Queue trafficFreeInitQueue() {
        return new Queue(trafficFreeInitQueue, true, false, false);
    }

    /**
     * 免费流量包新增队列 与交换机的绑定关系建立
     *
     * @return
     */
    @Bean
    public Binding trafficFreeInitQueueBinding() {
        return new Binding(trafficFreeInitQueue, Binding.DestinationType.QUEUE, trafficEventExchange, trafficFreeInitRoutingKey, null);
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

    /**
     * 延迟队列，不能被监听消费
     */
    private String trafficReleaseDelayQueue;

    /**
     * 进入延迟队列的路由key
     */
    private String trafficReleaseDelayRoutingKey;

    /**
     * 流量包恢复队列，延迟队列的消息过期后转发的队列-死信队列
     */
    private String trafficReleaseQueue;

    /**
     * 消息过期，进入死信队列的key
     */
    private String trafficReleaseRoutingKey;

    /**
     * 过期时间，毫秒,1分钟
     */
    private Integer ttl;


    /**
     * 延迟队列
     * 第一个队列:延迟队列，不能被监听消费
     *
     * @return
     */
    @Bean
    public Queue trafficReleaseDelayQueue() {
        Map<String, Object> args = new HashMap<>(3);

        args.put("x-dead-letter-exchange", trafficEventExchange);
        args.put("x-dead-letter-routing-key", trafficReleaseDelayRoutingKey);
        args.put("x-message-ttl", ttl);

        return new Queue(trafficReleaseDelayQueue, true, false, false, args);

    }

    /**
     * 死信队列(使用普通队列)
     * 被消费者监听的,流量包恢复队列
     * 延迟队列的消息过期后转发的队列
     *
     * @return
     */
    @Bean
    public Queue trafficReleaseQueue() {
        return new Queue(trafficReleaseQueue, true, false, false, null);
    }

    /**
     * 第一个队列:延迟队列 与交换机 的绑定关系建立
     *
     * @return
     */
    @Bean
    public Binding trafficReleaseDelayQueueBinding() {
        return new Binding(trafficReleaseDelayQueue, Binding.DestinationType.QUEUE, trafficEventExchange, trafficReleaseRoutingKey, null);
    }

    /**
     * 第二个队列:死信队列 与交换机 的绑定关系建立
     * @return
     */
    @Bean
    public Binding trafficReleaseQueueBinding() {
        return new Binding(trafficReleaseQueue, Binding.DestinationType.QUEUE, trafficEventExchange, trafficReleaseRoutingKey, null);
    }

}
