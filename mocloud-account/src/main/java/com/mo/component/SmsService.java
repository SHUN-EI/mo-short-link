package com.mo.component;

/**
 * Created by mo on 2022/2/8
 * 短信服务
 */
public interface SmsService {

    void sendSms(String to,String subject,String content);
}
