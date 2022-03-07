package com.mo.service.impl;

import com.mo.component.PayFactory;
import com.mo.config.RabbitMQConfig;
import com.mo.constant.TimeConstant;
import com.mo.enums.*;
import com.mo.exception.BizException;
import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.ProductManager;
import com.mo.manager.ProductOrderManager;
import com.mo.model.EventMessage;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
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
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    @Autowired
    private PayFactory payFactory;


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

        //发送延迟消息-用于自动关单
        sendOrderCloseMessage(outTradeNo, loginUserDTO.getAccountNo());

        //调用支付
        String codeUrl = payFactory.pay(payInfoVO);
        if (StringUtils.isNotBlank(codeUrl)) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("code_url", codeUrl);
            resultMap.put("out_trade_no", outTradeNo);
            return JsonData.buildSuccess(resultMap);
        }

        return JsonData.buildResult(BizCodeEnum.PAY_ORDER_FAIL);
    }

    /**
     * 延迟队列监听，关闭订单
     * <p>
     * 延迟消息的时间 需要比订单过期 时间长一点，这样就不存在查询的时候，用户还能支付成功
     * 1。查询订单是否存在，如果已经支付则正常结束
     * 2。如果订单未支付，主动调用第三方支付平台查询订单状态
     * 3。若确认未支付，本地取消订单
     * 4。如果第三方平台已经支付，主动的把订单状态改成已支付，但造成该原因的情况可能是支付通道回调有问题，然后触发支付后的动作，如何触发？RPC还是？
     *
     * @param eventMessage
     * @return
     */
    @Override
    public Boolean closeOrder(EventMessage eventMessage) {

        String outTradeNo = eventMessage.getBizId();
        Long accountNo = eventMessage.getAccountNo();

        ProductOrderDO productOrderDO = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        if (null == productOrderDO) {
            //订单不存在
            log.warn("订单不存在:{}", eventMessage);
            return true;
        }

        if (productOrderDO.getState().equalsIgnoreCase(OrderStateEnum.PAY.name())) {
            //订单已经支付
            log.info("直接确认消息，订单已经支付:{}", eventMessage);
            return true;
        }

        //未支付，需要向第三方支付平台查询状态
        if (productOrderDO.getState().equalsIgnoreCase(OrderStateEnum.NEW.name())) {
            //向第三方支付查询订单是否真的未支付
            PayInfoVO payInfoVO = PayInfoVO.builder()
                    .outTradeNo(outTradeNo)
                    .accountNo(accountNo)
                    .payType(productOrderDO.getPayType())
                    .build();

            //TODO 需要向第三方支付平台查询状态
            String payResult = "";

            if (StringUtils.isBlank(payResult)) {
                //查询支付结果为空，则未支付成功，更新订单状态为 CANCEL
                //造成该原因的情况可能是支付通道回调有问题
                productOrderManager.updateOrderPayState(outTradeNo, accountNo, OrderStateEnum.CANCEL.name(), OrderStateEnum.NEW.name());
                log.warn("查询支付结果为空，则未支付成功，本地取消订单:{}", eventMessage);
            } else {
                //支付成功，订单状态改成已支付
                productOrderManager.updateOrderPayState(outTradeNo, accountNo, OrderStateEnum.PAY.name(), OrderStateEnum.NEW.name());
            }
        }

        return true;
    }

    /**
     * 发送延迟消息-用于自动关单
     *
     * @param outTradeNo
     * @param accountNo
     */
    private void sendOrderCloseMessage(String outTradeNo, Long accountNo) {

        EventMessage eventMessage = EventMessage.builder()
                .eventMessageType(EventMessageTypeEnum.ORDER_NEW.name())
                .accountNo(accountNo)
                .bizId(outTradeNo)
                .build();

        rabbitTemplate.convertAndSend(rabbitMQConfig.getOrderEventExchange(),
                rabbitMQConfig.getOrderCloseDelayRoutingKey(), eventMessage);

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
     * 如果有优惠券或者其他抵扣
     * 验证前端显示和后台计算价格
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
