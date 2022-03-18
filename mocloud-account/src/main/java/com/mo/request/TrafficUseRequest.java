package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/3/16
 */
@ApiModel(value = "流量包扣减请求对象", description = "流量包扣减请求对象")
@Data
public class TrafficUseRequest {

    @ApiModelProperty(value = "账号")
    private Long accountNo;
    @ApiModelProperty(value = "业务id,短链码")
    private String bizId;

}
