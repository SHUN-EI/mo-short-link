package com.mo.aop;

import java.lang.annotation.*;

/**
 * Created by mo on 2022/3/2
 * 防重提交-自定义注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /**
     * 防重提交，支持两种，一个是方法参数，一个是令牌
     */
    enum Type {PARAM, TOKEN}

    /**
     * 防重提交，默认类型是方法参数
     *
     * @return
     */
    Type limitType() default Type.PARAM;

    /**
     * 加锁过期时间,默认是5秒
     *
     * @return
     */
    long lockTime() default 5;
}
