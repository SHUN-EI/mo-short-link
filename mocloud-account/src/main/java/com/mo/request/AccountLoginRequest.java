package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/2/15
 */
@ApiModel(value = "用户登录对象", description = "用户登录请求对象")
@Data
public class AccountLoginRequest {
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "密码")
    private String pwd;
}
