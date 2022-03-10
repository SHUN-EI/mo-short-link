package com.mo.service;

import com.mo.enums.OrderPayTypeEnum;
import com.mo.model.EventMessage;
import com.mo.request.CreateOrderRequest;
import com.mo.request.OrderListRequest;
import com.mo.utils.JsonData;

import java.util.Map;

/**
 * Created by mo on 2022/3/1
 */
public interface OrderService {
    Map<String, Object> pageOrderList(OrderListRequest request);

    String queryOrderState(String outTradeNo);

    JsonData createOrder(CreateOrderRequest request);

    Boolean closeOrder(EventMessage eventMessage);

    JsonData processOrderCallbackMsg(OrderPayTypeEnum orderPayType, Map<String, String> paramsMap);

    void processOrderMessage(EventMessage eventMessage);
}
