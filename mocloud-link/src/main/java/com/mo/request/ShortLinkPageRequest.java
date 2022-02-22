package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/2/22
 */
@ApiModel(value = "短链分页请求对象", description = "短链分页请求对象")
@Data
public class ShortLinkPageRequest {

    @ApiModelProperty(value = "当前页")
    private Integer page;
    @ApiModelProperty(value = "每页显示多少条")
    private Integer size;
    @ApiModelProperty(value = "账号唯一编号")
    private Long accountNo;
    @ApiModelProperty(value = "短链分组id")
    private Long groupId;
}
