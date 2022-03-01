package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/3/1
 */
@ApiModel(value = "订单列表对象", description = "订单列表请求对象")
@Data
public class OrderListRequest {

    @ApiModelProperty(value = "每页显示多少条")
    private Integer size;
    @ApiModelProperty(value = "当前页")
    private Integer page;
    /**
     * 订单状态，若不传的话，则查询全部订单
     */
    @ApiModelProperty(value = "订单状态")
    @JsonProperty("order_state")
    private String orderState;
}
