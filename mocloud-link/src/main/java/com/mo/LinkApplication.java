package com.mo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by mo on 2022/2/17
 */
@SpringBootApplication
@MapperScan("com.mo.mapper")
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients
public class LinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkApplication.class, args);
    }

}
