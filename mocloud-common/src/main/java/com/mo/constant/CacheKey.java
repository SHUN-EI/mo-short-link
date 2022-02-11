package com.mo.constant;

/**
 * Created by mo on 2022/2/11
 */
public class CacheKey {

    /**
     * 注册验证码缓存Key，第一个%s是类型，第二个%s是接收号码,唯一标识比如手机号或者邮箱
     */
    public static final String CHECK_CODE_KEY = "code:%s:%s";
}
