package com.mo.component;

import com.mo.vo.PayInfoVO;

/**
 * Created by mo on 2022/3/7
 */
public class PayStrategyContext {

    private PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy) {
        this.payStrategy = payStrategy;
    }


    /**
     * 根据支付策略，调用不同的支付接口
     *
     * @param payInfoVO
     * @return
     */
    public String executeUnifiedOrder(PayInfoVO payInfoVO) {
        return payStrategy.unifiedOrder(payInfoVO);
    }

    /**
     * 根据策略对象，执行不同的查询订单状态接口
     *
     * @param payInfoVO
     * @return
     */
    public String executeQueryPayStatus(PayInfoVO payInfoVO) {
        return payStrategy.queryPayStatus(payInfoVO);
    }

    /**
     * 根据策略对象，执行不同的退款接口
     *
     * @param payInfoVO
     * @return
     */
    public String executeRefund(PayInfoVO payInfoVO) {
        return payStrategy.refund(payInfoVO);
    }

    /**
     * 根据策略对象，执行不同的关闭接口
     *
     * @param payInfoVO
     * @return
     */
    public String executeCloseOrder(PayInfoVO payInfoVO) {
        return payStrategy.closeOrder(payInfoVO);
    }

}
