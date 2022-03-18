package com.mo.constant;

/**
 * Created by mo on 2022/3/2
 */
public class TimeConstant {

    /**
     * 支付订单的有效时长，超过时间未支付则关闭订单
     * 订单超时时间，毫秒，默认30分钟  30 * 60 * 1000
     * 为了方便测试，这里改为5分钟
     */
    public static final long ORDER_PAY_TIMEOUT_MILLS = 5 * 60 * 1000;

    public static final String DATE_YYYY_MM_DD = "yyyy-MM-dd";
}
