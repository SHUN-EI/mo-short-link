package com.mo.service.impl;

import com.mo.service.NotifyService;
import com.mo.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2022/2/9
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 模拟测试发送短信验证码接口
     */
    @Override
    @Async("threadPoolTaskExecutor")
    public void sendTest() {

        //方法1:线程睡眠，模拟接口处理响应时间
//        try {
//            TimeUnit.MILLISECONDS.sleep(2000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //方法2:请求别的接口，模拟接口处理响应时间
        long startTime = CommonUtil.getCurrentTimestamp();
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://old.xdclass.net", String.class);
        String body = forEntity.getBody();

        long endTime = CommonUtil.getCurrentTimestamp();
        log.info("耗时={},body={}", endTime - startTime, body);

    }
}
