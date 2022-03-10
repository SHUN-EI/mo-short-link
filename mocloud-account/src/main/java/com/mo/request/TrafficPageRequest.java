package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/3/10
 */
@ApiModel(value = "流量包分页请求对象", description = "流量包分页请求对象")
@Data
public class TrafficPageRequest {

    @ApiModelProperty(value = "当前页")
    private Integer page;
    @ApiModelProperty(value = "每页显示多少条")
    private Integer size;
    @ApiModelProperty(value = "账号唯一编号")
    private Long accountNo;
}
