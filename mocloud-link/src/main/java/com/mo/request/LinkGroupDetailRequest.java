package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/2/17
 */
@ApiModel(value = "短链分组详情/删除请求对象", description = "短链分组详情/删除请求对象")
@Data
public class LinkGroupDetailRequest {

    @ApiModelProperty(value = "短链分组id")
    private Long id;
}
