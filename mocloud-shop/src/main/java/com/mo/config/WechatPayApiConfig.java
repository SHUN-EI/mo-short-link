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

    /**
     * Native订单状态查询, 根据商户订单号查询
     */
    public static final String NATIVE_QUERY = HOST+ "/v3/pay/transactions/out-trade-no/%s?mchid=%s";

    /**
     * Native订单-关闭订单
     */
    public static final String NATIVE_CLOSE_ORDER = HOST+ "/v3/pay/transactions/out-trade-no/%s/close";

    /**
     *  Native订单-申请退款
     */
    public static final String NATIVE_REFUND_ORDER = HOST+ "/v3/refund/domestic/refunds";



}
