package com.mo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2022/3/3
 */
@Configuration
public class RedissionConfig {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;
    @Value("${spring.redis.password}")
    private String redisPwd;

    /**
     * 配置分布式锁的RedissonClient
     * @return
     */
    @Bean
    public RedissonClient redissonClient(){

        Config config = new Config();

        //单机模式
        config.useSingleServer().setPassword(redisPwd).setAddress("redis://" + redisHost + ":" + redisPort);

        //集群模式
//        config.useClusterServers().setScanInterval(2000)
//                .addNodeAddress("127.0.0.1:8000", "127.0.0.1:8001", "127.0.0.1:8002");


        RedissonClient client = Redisson.create(config);
        return client;

    }

    /**
     * 集群模式
     * 备注：可以用"rediss://"来启用SSL连接
     */
    /*@Bean
    public RedissonClient redissonClusterClient() {
        Config config = new Config();
        config.useClusterServers().setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
              .addNodeAddress("redis://127.0.0.1:7000")
              .addNodeAddress("redis://127.0.0.1:7002");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }*/
}
