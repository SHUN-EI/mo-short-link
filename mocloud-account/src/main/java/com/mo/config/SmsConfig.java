package com.mo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2022/2/8
 */
@ConfigurationProperties(prefix = "sms")
@Configuration
@Data
public class SmsConfig {

    private String templateId;

    private String appCode;

    private String url;
}
