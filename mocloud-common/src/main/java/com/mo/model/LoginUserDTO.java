package com.mo.model;

import lombok.Builder;
import lombok.Data;

/**
 * Created by mo on 2022/2/15
 * 用户登录对象
 */
@Data
@Builder
public class LoginUserDTO {

    /**
     * 账号
     */
    private long accountNo;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 认证级别
     */
    private String auth;
}
