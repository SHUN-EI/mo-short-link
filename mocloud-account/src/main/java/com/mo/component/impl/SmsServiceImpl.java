package com.mo.component.impl;

import com.mo.component.SmsService;
import com.mo.config.SmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by mo on 2022/2/8
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SmsConfig smsConfig;

    /**
     * 发送短信
     *
     * @param to
     * @param subject
     * @param content
     */
    @Override
    public void sendSms(String to, String subject, String content) {
        //拼接短信发送的url
        String url = smsConfig.getUrl() + "?mobile=" + to + "&templateId=" + subject + "&value=" + content;

        HttpHeaders headers = new HttpHeaders();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.set("Authorization", "APPCODE " + smsConfig.getAppCode());

        HttpEntity entity = new HttpEntity<>(headers);
        //发送
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        log.info("url={},body={}", url, response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("发送短信验证码成功:{}", response.getBody());
        } else {
            log.info("发送短信验证码失败:{}", response.getBody());
        }
    }
}
