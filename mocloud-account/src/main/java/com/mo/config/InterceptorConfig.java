package com.mo.config;

import com.mo.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by mo on 2022/2/15
 */
@Configuration
public class InterceptorConfig  implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                //拦截路径
                .addPathPatterns("/api/account/*/**", "/api/traffic/*/**")
                //不拦截路径
                .excludePathPatterns(
                        "/api/notify/*/getCaptcha",
                        "/api/notify/*/sendCode",
                        "/api/account/*/login",
                        "/api/account/*/register",
                        "/api/account/*/upload");
    }
}
