package com.mo.manager;

import com.mo.model.ProductOrderDO;

import java.util.Map;

/**
 * Created by mo on 2022/3/1
 */
public interface ProductOrderManager {

    Integer add(ProductOrderDO productOrderDO);

    ProductOrderDO findByOutTradeNoAndAccountNo(String outTradeNo,Long accountNo);

    Integer updateOrderPayState(String outTradeNo,Long accountNo,String newState,String oldState);

    Map<String,Object> page(int page, int size, Long accountNo, String state);

    Integer delete(Long productOrderId,Long accountNo);
}
