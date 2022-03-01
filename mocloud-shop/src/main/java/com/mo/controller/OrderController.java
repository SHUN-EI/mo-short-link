package com.mo.controller;

import com.mo.enums.BizCodeEnum;
import com.mo.request.CreateOrderRequest;
import com.mo.request.OrderListRequest;
import com.mo.service.OrderService;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by mo on 2022/3/1
 */
@Api(tags = "订单模块")
@RestController
@RequestMapping("/api/order/v1")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("/createOrder")
    public void createOrder(@ApiParam("创建订单对象") @RequestBody CreateOrderRequest request, HttpServletResponse response) {

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
