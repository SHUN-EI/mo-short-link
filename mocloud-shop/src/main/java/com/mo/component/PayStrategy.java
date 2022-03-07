package com.mo.component;

import com.mo.vo.PayInfoVO;

/**
 * Created by mo on 2022/3/7
 * 支付策略接口
 */
public interface PayStrategy {

    /**
     * 统一下单接口
     *
     * @param payInfoVO
     * @return
     */
    String unifiedOrder(PayInfoVO payInfoVO);

    /**
     * 退款接口
     *
     * @param payInfoVO
     * @return
     */
    default String refund(PayInfoVO payInfoVO) {
        return "";
    }

    /**
     * 查询支付状态
     *
     * @return
     */
    default String queryPayStatus(PayInfoVO payInfoVO) {
        return "";
    }

    /**
     * 关闭订单
     *
     * @param payInfoVO
     * @return
     */
    default String closeOrder(PayInfoVO payInfoVO) {
        return "";
    }

}
