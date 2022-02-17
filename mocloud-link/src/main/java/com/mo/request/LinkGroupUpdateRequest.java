package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/2/17
 */
@ApiModel(value = "短链分组更新请求对象", description = "短链分组更新请求对象")
@Data
public class LinkGroupUpdateRequest {

    @ApiModelProperty(value = "短链分组id")
    private Long id;
    @ApiModelProperty(value = "组名")
    private String title;
}
