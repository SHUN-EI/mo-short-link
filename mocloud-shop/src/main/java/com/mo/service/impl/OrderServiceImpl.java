package com.mo.service.impl;

import com.mo.constant.TimeConstant;
import com.mo.enums.*;
import com.mo.exception.BizException;
import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.ProductManager;
import com.mo.manager.ProductOrderManager;
import com.mo.model.LoginUserDTO;
import com.mo.model.ProductDO;
import com.mo.model.ProductOrderDO;
import com.mo.request.CreateOrderRequest;
import com.mo.request.OrderListRequest;
import com.mo.service.OrderService;
import com.mo.utils.JsonData;
import com.mo.utils.JsonUtil;
import com.mo.utils.OrderCodeGenerateUtil;
import com.mo.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by mo on 2022/3/1
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderManager productOrderManager;
    @Autowired
    private ProductManager productManager;
    @Autowired
    private OrderCodeGenerateUtil orderCodeGenerateUtil;

    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData createOrder(CreateOrderRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        //生成订单号
        String outTradeNo = orderCodeGenerateUtil.generateOrderCode(OrderCodeEnum.XD);

        ProductDO productDO = productManager.findById(request.getProductId());

        //订单验证价格，后端需要计算校验订单价格，不能单以前端为准
        checkPrice(productDO, request);

        //创建订单
        ProductOrderDO productOrderDO = saveOrder(request, loginUserDTO, outTradeNo, productDO);

        //创建支付信息-对接第三方支付
        PayInfoVO payInfoVO = PayInfoVO.builder()
                .outTradeNo(outTradeNo)
                .accountNo(loginUserDTO.getAccountNo())
                .clientType(request.getClientType())
                .payType(request.getPayType())
                .title(productDO.getTitle())
                .description(outTradeNo)
                .payFee(request.getPayAmount())
                .orderPayTimeoutMills(TimeConstant.ORDER_PAY_TIMEOUT_MILLS)
                .build();


        return null;
    }

    /**
     * 创建订单
     *
     * @param request
     * @param loginUserDTO
     * @param outTradeNo
     * @param productDO
     * @return
     */
    private ProductOrderDO saveOrder(CreateOrderRequest request, LoginUserDTO loginUserDTO, String outTradeNo, ProductDO productDO) {

        ProductOrderDO productOrderDO = ProductOrderDO.builder()
                //用户信息
                .accountNo(loginUserDTO.getAccountNo())
                .nickname(loginUserDTO.getUsername())
                //商品信息
                .productId(productDO.getId())
                .productTitle(productDO.getTitle())
                //商品快照
                .productSnapshot(JsonUtil.obj2Json(productDO))
                .productAmount(productDO.getAmount())
                //订单信息
                .buyNum(request.getBuyNum())
                .outTradeNo(outTradeNo)
                .del(0)
                //发票信息
                .billType(BillTypeEnum.valueOf(request.getBillType()).name())
                .billHeader(request.getBillHeader())
                .billReceiverPhone(request.getBillReceiverPhone())
                .billReceiverEmail(request.getBillReceiverEmail())
                .billContent(request.getBillContent())
                //实际支付总价
                .payAmount(request.getPayAmount())
                //总价，若没使用优惠券
                .totalAmount(request.getTotalAmount())
                .state(OrderStateEnum.NEW.name())
                .payType(OrderPayTypeEnum.valueOf(request.getPayType()).name())
                .build();


        productOrderManager.add(productOrderDO);

        return productOrderDO;
    }

    /**
     * 订单验证价格
     *
     * @param productDO
     * @param request
     */
    private void checkPrice(ProductDO productDO, CreateOrderRequest request) {

        BigDecimal finalAmount = BigDecimal.ZERO;
        //购买数量
        Integer buyNum = request.getBuyNum();

        finalAmount = BigDecimal.valueOf(buyNum).multiply(productDO.getAmount());

        //后台计算的实际支付价格与前端传过来的实际支付价格比较，若不相等，则验价失败，不能创建订单
        //前端传递总价和后端计算总价格是否一致, 如果有优惠券，也在这里进行计算
        if (finalAmount.compareTo(request.getPayAmount()) != 0) {
            log.error("订单验价失败:{}", request);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_PRICE_FAIL);
        }

    }

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
