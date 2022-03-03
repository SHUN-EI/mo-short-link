package com.mo.controller;

import com.mo.aop.RepeatSubmit;
import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.interceptor.LoginInterceptor;
import com.mo.model.LoginUserDTO;
import com.mo.request.CreateOrderRequest;
import com.mo.request.OrderListRequest;
import com.mo.service.OrderService;
import com.mo.utils.CommonUtil;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2022/3/1
 */
@Api(tags = "订单模块")
@RestController
@RequestMapping("/api/order/v1")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;


    @ApiOperation("创建订单")
    @PostMapping("/createOrder")
    //@RepeatSubmit(limitType = RepeatSubmit.Type.PARAM)
    public void createOrder(@ApiParam("创建订单对象") @RequestBody CreateOrderRequest request, HttpServletResponse response) {
        JsonData jsonData = orderService.createOrder(request);

        if (jsonData.getCode() == 0) {
            log.info("创建订单成功:{}", jsonData.getData());
        } else {
            log.error("创建订单失败:{}", jsonData.getData());
            CommonUtil.sendJsonMessage(response, jsonData);
        }
    }

    @ApiOperation("获取提交订单令牌")
    @GetMapping("/getOrderSubmitToken")
    public JsonData getOrderSubmitToken() {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        String token = CommonUtil.getStringNumRandom(32);
        //key是 order:submit:accountNo:token,然后直接删除成功则完成
        String key = String.format(CacheKey.ORDER_REPEAT_SUBMIT_TOKEN_KEY, loginUserDTO.getAccountNo(), token);

        //令牌有效时间是30分钟
        redisTemplate.opsForValue().set(key, token, 30, TimeUnit.MINUTES);

        return JsonData.buildSuccess(token);

    }

    @ApiOperation("分页查询订单列表")
    @PostMapping("/pageOrderList")
    public JsonData pageOrderList(@ApiParam("订单列表对象") @RequestBody OrderListRequest request) {

        Map<String, Object> pageResult = orderService.pageOrderList(request);
        return JsonData.buildSuccess(pageResult);
    }


    /**
     * 查询订单状态
     * 此接口没有登录拦截，远程调用考虑安全的话，可以增加一个密钥进行RPC通信
     * 不加密钥也行，因为此接口只是返回一个订单的状态，没有太多敏感信息
     *
     * @param outTradeNo
     * @return
     */
    @ApiOperation("查询订单状态")
    @GetMapping("/queryOrderState")
    public JsonData queryOrderState(@ApiParam("订单号") @RequestParam("out_trade_no") String outTradeNo) {

        String state = orderService.queryOrderState(outTradeNo);
        return StringUtils.isBlank(state)
                ? JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST)
                : JsonData.buildSuccess(state);

    }


}
