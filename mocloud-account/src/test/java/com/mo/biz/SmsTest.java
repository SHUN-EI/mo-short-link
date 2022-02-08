package com.mo.biz;

import com.mo.AccountApplication;
import com.mo.component.SmsService;
import com.mo.config.SmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by mo on 2022/2/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@Slf4j
public class SmsTest {

    @Autowired
    private SmsService smsService;
    @Autowired
    private SmsConfig smsConfig;

    @Test
    public void testSendSms() {
        smsService.sendSms("18098945397",smsConfig.getTemplateId(),"666888");
    }
}
