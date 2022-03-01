package com.mo.service.impl;

import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.ProductOrderManager;
import com.mo.model.LoginUserDTO;
import com.mo.model.ProductOrderDO;
import com.mo.request.OrderListRequest;
import com.mo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by mo on 2022/3/1
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderManager productOrderManager;

    /**
     * 查询订单状态
     *
     * @param outTradeNo
     * @return
     */
    @Override
    public String queryOrderState(String outTradeNo) {
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        ProductOrderDO productOrderDO = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, loginUserDTO.getAccountNo());
        return productOrderDO == null ? "" : productOrderDO.getState();
    }

    /**
     * 分页查询订单列表
     *
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> pageOrderList(OrderListRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        Map<String, Object> pageResult = productOrderManager.page(request.getPage(), request.getSize(), loginUserDTO.getAccountNo(), request.getOrderState());
        return pageResult;
    }
}
