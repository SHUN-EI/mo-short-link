package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/2/26
 */
@ApiModel(value = "短链更新请求对象", description = "短链更新请求对象")
@Data
public class ShortLinkUpdateRequest {

    @ApiModelProperty(value = "短链分组id")
    private Long groupId;

    @ApiModelProperty(value = "短链id-B端")
    private Long mappingId;

    @ApiModelProperty(value = "短链码")
    private String code;

    @ApiModelProperty(value = "短链标题")
    private String title;

    @ApiModelProperty(value = "域名id")
    private Long domainId;

    @ApiModelProperty(value = "域名类型")
    private String domainType;
}
