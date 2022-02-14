package com.mo.service;

import com.mo.enums.SendCodeEnum;
import com.mo.utils.JsonData;

/**
 * Created by mo on 2022/2/9
 */
public interface NotifyService {


    /**
     * 发送验证码
     * @param sendCodeEnum
     * @param to
     * @return
     */
    JsonData sendCode(SendCodeEnum sendCodeEnum, String to);

    /**
     * 校验验证码
     * @param sendCodeEnum
     * @param to
     * @param code
     * @return
     */
    Boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code);

    /**
     * 用于测试
     */
    void sendTest();


}
