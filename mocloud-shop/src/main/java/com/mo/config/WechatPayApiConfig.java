package com.mo.config;

/**
 * Created by mo on 2022/3/4
 */
public class WechatPayApiConfig {


    /**
     * 微信支付主机地址-域名
     */
    public static final String HOST = "https://api.mch.weixin.qq.com";

    /**
     * Native下单地址
     */
    public static final String NATIVE_ORDER = HOST + "/v3/pay/transactions/native";


}
