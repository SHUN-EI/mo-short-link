package com.mo.request;

import com.mo.enums.ShortLinkStateEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * Created by mo on 2022/2/22
 */
@ApiModel(value = "短链更新请求对象", description = "短链更新请求对象")
@Data
public class GroupCodeMappingUpdateRequest {

    @ApiModelProperty(value = "账号唯一编号")
    private Long accountNo;
    @ApiModelProperty(value = "短链分组id")
    private Long groupId;
    @ApiModelProperty(value = "短链码")
    private String shortLinkCode;
    @ApiModelProperty(value = "短链码状态")
    private ShortLinkStateEnum shortLinkStateEnum;
}
