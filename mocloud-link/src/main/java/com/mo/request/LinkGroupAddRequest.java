package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/2/17
 */
@ApiModel(value = "添加短链分组请求对象", description = "添加短链分组请求对象")
@Data
public class LinkGroupAddRequest {

    @ApiModelProperty(value = "短链分组名")
    private String title;
}
