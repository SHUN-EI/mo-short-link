package com.mo.utils;

import com.mo.constant.CacheKey;
import com.mo.constant.OrderCodeConstant;
import com.mo.enums.OrderCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2022/3/2
 */
@Component
public class OrderCodeGenerateUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据订单编号类型，自动生成订单编号
     *
     * @param orderCodeEnum
     * @return
     */
    public String generateOrderCode(OrderCodeEnum orderCodeEnum) {

        //订单类型前缀
        String orderTypePrefix = getOrderTypePrefix(orderCodeEnum);
        //拼接Redis缓存key
        String cacheKey = getCacheKey(orderTypePrefix);
        //构建流水号
        Long incrementalSerial = incr(cacheKey, OrderCodeConstant.DEFAULT_CACHE_DAYS, TimeUnit.DAYS);
        //设置Key的过期时间
        expireKey(cacheKey, OrderCodeConstant.DEFAULT_CACHE_DAYS, TimeUnit.DAYS);
        //拼接流水号
        String serialWithPrefix = completionSerial(orderTypePrefix, incrementalSerial, orderCodeEnum);
        //拼接随机数
        String orderCode = completionRandom(orderCodeEnum, serialWithPrefix);

        return orderCode;
    }

    /**
     * 生成订单号前缀
     *
     * @param orderCodeEnum
     * @return
     */
    private String getOrderTypePrefix(OrderCodeEnum orderCodeEnum) {
        //多线程下操作⼤量的字符串，且需要保证线程安全 则⽤StringBuffer
        StringBuffer sb = new StringBuffer();
        sb.append(orderCodeEnum.getPrefix());
        sb.append(DateUtil.getCurrentTime(orderCodeEnum.getDatePattern()));
        return sb.toString();
    }

    /**
     * 构建流水号缓存key
     *
     * @param serialPrefix
     * @return
     */
    private String getCacheKey(String serialPrefix) {
        return CacheKey.ORDER_CODE_CACHE_PREFIX_KEY.concat(serialPrefix);
    }

    /**
     * Redis 中的key存储的数字值+1(默认是增量+1)
     * Redis存储的流水号加1
     *
     * @param key
     * @param duration
     * @param timeUtil
     * @return
     */
    private Long incr(String key, int duration, TimeUnit timeUtil) {
        validateKeyParam(key);

        RedisAtomicLong idCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = idCounter.getAndIncrement();

        //时间转换成毫秒,过期时间
        Long expireTime = DateUtil.getMillis(timeUtil, duration);

        //初始设置过期时间
        if ((null == increment || increment.longValue() == 0) && expireTime > 0) {
            idCounter.expire(expireTime, TimeUnit.DAYS);
        }

        return increment;
    }

    /**
     * 补全流水号-拼接流水号
     *
     * @param serialPrefix
     * @param incrementalSerial
     * @param orderCodeEnum
     * @return
     */
    private String completionSerial(String serialPrefix, Long incrementalSerial, OrderCodeEnum orderCodeEnum) {
        StringBuffer sb = new StringBuffer(serialPrefix);

        //需要补0的长度=流水号长度-当日自增计数长度
        //当日自增的长度
        int incrementalSerialLength = String.valueOf(incrementalSerial).length();
        int length = orderCodeEnum.getSerialLength() - incrementalSerialLength;

        //补0
        for (int i = 0; i < length; i++) {
            sb.append("0");
        }

        //redis当日自增数
        sb.append(incrementalSerial);

        return sb.toString();
    }

    /**
     * 补全随机数——拼接随机数
     *
     * @param orderCodeEnum
     * @param serialwithPrefix
     * @return
     */
    private String completionRandom(OrderCodeEnum orderCodeEnum, String serialwithPrefix) {

        StringBuffer sb = new StringBuffer(serialwithPrefix);

        //随机数长度
        Integer randomLength = orderCodeEnum.getRandomLength();
        if (randomLength > 0) {
            Random random = new Random();
            for (int i = 0; i < randomLength; i++) {
                //十以内随机数补全(0-9)
                sb.append(random.nextInt(10));
            }
        }
        return sb.toString();
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param time
     * @param timeUnit
     */
    private void expireKey(String key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }


    /**
     * redis参数校验
     *
     * @param key
     */
    private void validateKeyParam(String key) {
        Assert.hasText(key, "key不能为空");
        Assert.notNull(redisTemplate, "redis连接初始化失败");
    }


}
