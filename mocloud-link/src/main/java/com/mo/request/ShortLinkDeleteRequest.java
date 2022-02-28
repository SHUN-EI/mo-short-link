package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/2/26
 */
@ApiModel(value = "短链删除请求对象", description = "短链删除请求对象")
@Data
public class ShortLinkDeleteRequest {

    @ApiModelProperty(value = "短链分组id")
    private Long groupId;
    @ApiModelProperty(value = "短链id-B端")
    private Long mappingId;
    @ApiModelProperty(value = "短链码")
    private String code;
}
