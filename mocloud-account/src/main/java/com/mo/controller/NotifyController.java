package com.mo.controller;

import com.mo.service.NotifyService;
import com.mo.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mo on 2022/2/9
 */
@RestController
@RequestMapping("/api/account/v1")
@Slf4j
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    /**
     * 测试发送短信验证码接口-主要是用于对比优化前后区别
     * @return
     */
    @RequestMapping("/testSend")
    public JsonData testSend() {
        notifyService.sendTest();
        return JsonData.buildSuccess();
    }
}
