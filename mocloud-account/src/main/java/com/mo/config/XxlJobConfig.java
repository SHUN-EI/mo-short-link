package com.mo.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2022/3/13
 */
//@Configuration
@Data
@Slf4j
public class XxlJobConfig {

    /**
     * 调度中心部署地址
     */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    /**
     * 执行器app名称,和控制台那边配置一样的名称，不然注册不上去
     */
    @Value("${xxl.job.executor.appname}")
    private String appName;

    /**
     * [选填]执行器IP ：默认为空表示自动获取IP
     */
    @Value("${xxl.job.executor.ip}")
    private String ip;

    /**
     * [选填]执行器端口号：小于等于0则自动获取；默认端口为9999
     */
    @Value("${xxl.job.executor.port}")
    private int port;

    /**
     * 执行器token，非空时启用 xxl-job, access token
     */
    @Value("${xxl.job.accessToken}")
    private String accessToken;

    /**
     * 执行器日志文件存储路径
     */
    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    /**
     * 执行器日志保存天数
     */
    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;

    //旧版的有bug
    //@Bean(initMethod = "start", destroyMethod = "destroy")
    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(){

        log.info(">>>>>>>>>>>>> xxl-job config init >>>>>>>>>>>>>>>");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return xxlJobSpringExecutor;

    }
}
