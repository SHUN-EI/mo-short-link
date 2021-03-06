package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by mo on 2022/2/23
 */
@ApiModel(value = "短链新增请求对象", description = "短链新增请求对象")
@Data
public class ShortLinkAddRequest {

    @ApiModelProperty(value = "短链分组id")
    private Long groupId;

    @ApiModelProperty(value = "短链标题")
    private String title;

    @ApiModelProperty(value = "原生url")
    private String originalUrl;

    @ApiModelProperty(value = "域名id")
    private Long domainId;

    @ApiModelProperty(value = "域名类型")
    private String domainType;

    @ApiModelProperty(value = "过期时间")
    private Date expired;
}
