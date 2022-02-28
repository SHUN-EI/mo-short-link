package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/2/28
 */
@ApiModel(value = "商品分页请求对象", description = "商品分页请求对象")
@Data
public class ProductPageRequest {

    @ApiModelProperty(value = "当前页")
    private Integer page;
    @ApiModelProperty(value = "每页显示多少条")
    private Integer size;
}
