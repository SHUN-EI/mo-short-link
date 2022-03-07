package com.mo.component;

import com.mo.enums.OrderPayTypeEnum;
import com.mo.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2022/3/7
 */
@Component
@Slf4j
public class PayFactory {

    @Autowired
    private AlipayStrategy alipayStrategy;
    @Autowired
    private WechatPayStrategy wechatPayStrategy;


    /**
     * 创建支付，简单工厂设计模式
     *
     * @return
     */
    public String pay(PayInfoVO payInfoVO) {
        String payType = payInfoVO.getPayType();
        if (OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            //支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);
            return payStrategyContext.executeUnifiedOrder(payInfoVO);
        } else if (OrderPayTypeEnum.WECHAT_PAY.name().equalsIgnoreCase(payType)) {
            //微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeUnifiedOrder(payInfoVO);
        }

        return "";
    }


    /**
     * 关闭订单
     *
     * @param payInfoVO
     * @return
     */
    public String closeOrder(PayInfoVO payInfoVO) {
        String payType = payInfoVO.getPayType();

        if (OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            //支付宝
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);
            return payStrategyContext.executeCloseOrder(payInfoVO);
        } else if (OrderPayTypeEnum.WECHAT_PAY.name().equalsIgnoreCase(payType)) {
            //微信
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeCloseOrder(payInfoVO);
        }

        return "";

    }

    /**
     * 退款
     *
     * @param payInfoVO
     * @return
     */
    public String refund(PayInfoVO payInfoVO) {
        String payType = payInfoVO.getPayType();

        if (OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            //支付宝
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);
            return payStrategyContext.executeRefund(payInfoVO);
        } else if (OrderPayTypeEnum.WECHAT_PAY.name().equalsIgnoreCase(payType)) {
            //微信
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeRefund(payInfoVO);
        }

        return "";

    }


    /**
     * 查询支付状态
     *
     * @param payInfoVO
     * @return
     */
    public String queryPayStatus(PayInfoVO payInfoVO) {
        String payType = payInfoVO.getPayType();

        if (OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            //支付宝
            PayStrategyContext payStrategyContext = new PayStrategyContext(alipayStrategy);
            return payStrategyContext.executeQueryPayStatus(payInfoVO);
        } else if (OrderPayTypeEnum.WECHAT_PAY.name().equalsIgnoreCase(payType)) {
            //微信
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);
            return payStrategyContext.executeQueryPayStatus(payInfoVO);
        }

        return "";

    }

}
