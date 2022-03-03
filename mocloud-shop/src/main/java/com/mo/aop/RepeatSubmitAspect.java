package com.mo.aop;

import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.exception.BizException;
import com.mo.interceptor.LoginInterceptor;
import com.mo.model.LoginUserDTO;
import com.mo.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2022/3/3
 * 切面处理类，防重提交判断处理
 */
@Aspect
@Component
@Slf4j
public class RepeatSubmitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;


    /**
     * 定义 @Pointcut注解表达式, 通过特定的规则来筛选连接点, 就是Pointcut，选中那几个你想要的方法
     * 在程序中主要体现为书写切入点表达式（通过通配、正则表达式）过滤出特定的一组 JointPoint连接点
     * <p>
     * 方式一：@annotation：当执行的方法上拥有指定的注解时生效（我们采用这）
     * 方式二：execution：一般用于指定方法的执行
     *
     * @param repeatSubmit
     */
    @Pointcut("@annotation(repeatSubmit)")
    public void pointCutRepeatSubmit(RepeatSubmit repeatSubmit) {
    }


    /**
     * 环绕通知, 围绕着方法执行
     *
     * @param joinPoint
     * @param RepeatSubmit
     * @param joinPoint
     * @param repeatSubmit
     * @return
     * @throws Throwable
     * @Around 可以用来在调用一个具体方法前和调用后来完成一些具体的任务。
     * <p>
     * 方式一：单用 @Around("execution(* com.mo.controller.*.*(..))")可以
     * 方式二：用@Pointcut和@Around联合注解也可以（我们采用这个）
     * <p>
     * <p>
     * 两种方式
     * 方式一：加锁 固定时间内不能重复提交
     * <p>
     * 方式二：先请求获取token，这边再删除token,删除成功则是第一次提交
     */
    @Around("pointCutRepeatSubmit(repeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, RepeatSubmit repeatSubmit) throws Throwable {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        //结果标记，用于记录成功或者失败
        Boolean result;

        //防重提交类型
        String type = repeatSubmit.limitType().name();
        if (type.equalsIgnoreCase(RepeatSubmit.Type.PARAM.name())) {
            //方式一，参数形式防重提交
            long lockTime = repeatSubmit.lockTime();
            //获取当前请求的ip
            String ipAddr = CommonUtil.getIpAddr(request);
            //userAgent
            String userAgent = request.getHeader("User-Agent");
            //通过反射机制获取切点处的方法
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            String className = method.getDeclaringClass().getName();

            //key的格式: ip+className+methodName+userAgent+user_id
            String keyFormat = String.format("%s-%s-%s-%s-%s", ipAddr, className, method, userAgent, loginUserDTO.getAccountNo());
            String key = CacheKey.ORDER_REPEAT_SUBMIT_KEY + CommonUtil.MD5(keyFormat);

            //redis存储,加锁
            //result = redisTemplate.opsForValue().setIfAbsent(key, "1", lockTime, TimeUnit.SECONDS);

            //使用分布式锁的RedissonClient,底层是lua脚本
            RLock lock = redissonClient.getLock(key);
            // 尝试加锁，最多等待0秒，上锁以后5秒自动解锁 [lockTime默认为5s, 可以自定义]
            result = lock.tryLock(0, lockTime, TimeUnit.SECONDS);

        } else {
            //方式二，令牌形式防重提交
            String requestToken = request.getHeader("request-token");
            if (StringUtils.isBlank(requestToken)) {
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_TOKEN_EQUAL_FAIL);
            }

            String key = String.format(CacheKey.ORDER_REPEAT_SUBMIT_TOKEN_KEY, loginUserDTO.getAccountNo(), requestToken);

            /**
             * 提交表单的token key
             * 方式一：不用lua脚本获取再判断，之前是因为 key组成是 order:submit:accountNo, value是对应的token，所以需要先获取值，再判断
             * 方式二：可以直接key是 order:submit:accountNo:token,然后直接删除成功则完成
             */
            result = redisTemplate.delete(key);
        }

        //取反
        if (!result) {
            log.error("创建订单请求重复提交");
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_REPEAT);
        }

        log.info("环绕通知执行前");
        Object proceed = joinPoint.proceed();
        log.info("环绕通知执行后");
        return proceed;
    }


}
