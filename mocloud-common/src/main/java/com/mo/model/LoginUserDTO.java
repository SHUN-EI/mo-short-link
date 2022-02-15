package com.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
     * 账号id(主键)
     */
    private Long id;

    /**
     * 账号
     */
    private Long accountNo;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    //@JsonProperty("head_img")
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

    public LoginUserDTO() {
    }

    public LoginUserDTO(Long id, Long accountNo, String username, String headImg, String mail, String phone, String auth) {
        this.id = id;
        this.accountNo = accountNo;
        this.username = username;
        this.headImg = headImg;
        this.mail = mail;
        this.phone = phone;
        this.auth = auth;

    }
}
