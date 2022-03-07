package com.mo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by mo on 2022/3/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PayInfoVO {

    /**
     * 订单号
     */
    //@JsonProperty("out_trade_no")
    private String outTradeNo;

    /**
     * 订单总金额 单位是分
     */
    private BigDecimal payFee;

    /**
     * 支付类型 微信-支付宝-银行-其他
     */
    private String payType;


    /**
     * 端类型，App/h5/pc
     */
    private String clientType;

    /**
     * 标题
     */
    private String title;

    /**
     * 详情
     */
    private String description;

    /**
     * 订单支付超时，毫秒
     */
    private Long orderPayTimeoutMills;


    /**
     * 用户标识
     */
    private Long accountNo;
}
