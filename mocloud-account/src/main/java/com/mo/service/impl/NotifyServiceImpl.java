package com.mo.service.impl;

import com.mo.component.SmsService;
import com.mo.config.SmsConfig;
import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.service.NotifyService;
import com.mo.utils.CheckUtil;
import com.mo.utils.CommonUtil;
import com.mo.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SmsConfig smsConfig;


    /**
     * 验证码过期时间，10min有效
     */
    private static final int CODE_EXPIRED = 60 * 1000 * 10;

    /**
     * 发送验证码（短信验证码或邮箱验证码）
     *
     * @param sendCodeEnum
     * @param to
     * @return
     */
    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {
        //缓存Key
        String cacheKey = String.format(CacheKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = (String) redisTemplate.opsForValue().get(cacheKey);

        //当前时间戳
        long currentTimestamp = CommonUtil.getCurrentTimestamp();
        //如果cacheValue 不为空，则判断是否60s内重复发送, 验证码格式: 0122_232131321314132
        if (StringUtils.isNoneBlank(cacheValue)) {

            long ttl = Long.parseLong(cacheValue.split("_")[1]);
            //当前时间戳-验证码发送时间戳，如果小于60秒，则不给重复发送
            if (currentTimestamp - ttl < 60 * 1000) {
                log.info("重复发送验证码,时间间隔:{} 秒", (currentTimestamp - ttl) / 1000);
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }

        //6位验证码
        String code = CommonUtil.getRandomCode(6);
        //拼接验证码 eg:8868_3273767673367
        String value = code + "_" + currentTimestamp;
        redisTemplate.opsForValue().set(cacheKey, value, CODE_EXPIRED, TimeUnit.MILLISECONDS);

        if (CheckUtil.isPhone(to)) {
            //发送短信验证码
            smsService.sendSms(to, smsConfig.getTemplateId(), code);
            return JsonData.buildSuccess(code);
        } else if (CheckUtil.isEmail(to)) {
            //发送邮箱验证码
        }

        //接收号码不合规范
        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }

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
