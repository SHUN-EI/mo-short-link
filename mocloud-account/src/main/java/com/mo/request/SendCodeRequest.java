package com.mo.request;

import lombok.Data;

/**
 * Created by mo on 2022/2/11
 * 发送验证码请求对象
 */
@Data
public class SendCodeRequest {

    private String captcha;

    private String to;
}
