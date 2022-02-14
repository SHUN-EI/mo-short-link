package com.mo.controller;

import com.google.code.kaptcha.Producer;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.request.SendCodeRequest;
import com.mo.service.NotifyService;
import com.mo.utils.CommonUtil;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2022/2/9
 */
@Api(tags = "通知模块")
@RestController
@RequestMapping("/api/account/v1")
@Slf4j
public class NotifyController {

    @Autowired
    private NotifyService notifyService;
    @Autowired
    private Producer captchaProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 图形验证码有效期为10分钟
     */
    private static final long CAPTCHA_CODE_EXPIRED = 60 * 1000 * 10;


    /**
     * 发送验证码（短信验证码或邮箱验证码）
     * <p>
     * 1.匹配图形验证码是否正常
     * 2.发送验证码
     *
     * @param request
     * @return
     */
    @ApiOperation("发送验证码")
    @PostMapping("/sendCode")
    public JsonData sendCode(@ApiParam("发送验证码请求对象")
                             @RequestBody SendCodeRequest request,
                             HttpServletRequest servletRequest) {
        //Redis缓存的key
        String captchaKey = getCaptchaKey(servletRequest);
        //Redis保存的验证码
        String cacheCaptcha = (String) redisTemplate.opsForValue().get(captchaKey);

        String captcha = request.getCaptcha();

        //匹配图形验证码
        if (captcha != null && cacheCaptcha != null && captcha.equalsIgnoreCase(cacheCaptcha)) {
            //删除缓存中的验证码,不删除的话就过期时间自动删除
            redisTemplate.delete(captchaKey);

            //发送验证码
            JsonData jsonData = notifyService.sendCode(SendCodeEnum.USER_REGISTER, request.getTo());
            return jsonData;
        } else {
            return JsonData.buildResult(BizCodeEnum.CODE_CAPTCHA_ERROR);
        }
    }

    /**
     * 获取图形验证码
     *
     * @param request
     * @param response
     */
    @ApiOperation("获取图形验证码")
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {

        //Redis缓存的key
        String cacheKey = getCaptchaKey(request);
        //验证码内容
        String captchaText = captchaProducer.createText();
        log.info("图形验证码:{}", captchaText);

        //存储
        redisTemplate.opsForValue().set(cacheKey, captchaText, CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);

        BufferedImage image = captchaProducer.createImage(captchaText);
        //try-with-resources,jdk1.7以后的语法，自动关闭流
        try (ServletOutputStream ops = response.getOutputStream()) {
            ImageIO.write(image, "jpg", ops);
            ops.flush();
        } catch (IOException e) {
            log.error("获取图形验证码异常:{}", e);
        }

    }


    /**
     * 获取Redis缓存的key
     *
     * @param request
     * @return
     */
    private String getCaptchaKey(HttpServletRequest request) {
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");

        String key = "user-service:captcha:" + CommonUtil.MD5(ip + userAgent);
        log.info("ip={}", ip);
        log.info("UserAgent={}", userAgent);
        log.info("key={}", key);

        return key;
    }

    /**
     * 测试发送短信验证码接口-主要是用于对比优化前后区别
     *
     * @return
     */
    @RequestMapping("/testSend")
    public JsonData testSend() {
        notifyService.sendTest();
        return JsonData.buildSuccess();
    }
}
