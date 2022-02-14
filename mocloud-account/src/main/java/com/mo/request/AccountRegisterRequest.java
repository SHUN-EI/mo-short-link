package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2022/2/14
 * 用户注册请求对象
 */
@ApiModel(value = "用户注册对象", description = "用户注册请求对象")
@Data
public class AccountRegisterRequest {

    @ApiModelProperty(value = "头像")
    private String headImg;

    @ApiModelProperty(value = "账号标识，判断是手机注册还是邮箱注册")
    private String to;

    @ApiModelProperty(value = "手机号", example = "13812341234")
    private String phone;

    @ApiModelProperty(value = "密码", example = "12345")
    private String pwd;

    @ApiModelProperty(value = "邮箱")
    private String mail;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "短信验证码")
    private String code;
}
