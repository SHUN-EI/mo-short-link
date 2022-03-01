package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by mo on 2022/3/1
 */
@ApiModel(value = "创建订单对象", description = "创建订单请求对象")
@Data
public class CreateOrderRequest {

    @ApiModelProperty("商品id")
    private Long productId;

    @ApiModelProperty("购买数量")
    private Integer buyNum;;

    @ApiModelProperty("终端类型")
    private String clientType;

    @ApiModelProperty("支付类型，微信-银行-支付宝")
    private String payType;

    @ApiModelProperty("订单总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty("订单实际支付价格")
    private BigDecimal payAmount;


    @ApiModelProperty("防止重复提交的令牌")
    private String token;


    @ApiModelProperty("发票类型：0->不开发票；1->电子发票；2->纸质发票")
    private String billType;

    @ApiModelProperty("发票抬头")
    private String billHeader;

    @ApiModelProperty("发票内容")
    private String billContent;

    @ApiModelProperty("发票收票人电话")
    private String billReceiverPhone;

    @ApiModelProperty("发票收票人邮箱")
    private String billReceiverEmail;
}
