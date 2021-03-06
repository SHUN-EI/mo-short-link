package com.mo.enums;

/**
 * Created by mo on 2022/2/23
 */
public enum EventMessageTypeEnum {
    /**
     * 短链创建
     */
    SHORT_LINK_ADD,

    /**
     * 短链创建 C端
     */
    SHORT_LINK_ADD_LINK,

    /**
     * 短链创建 B端
     */
    SHORT_LINK_ADD_MAPPING,

    /**
     * 短链更新
     */
    SHORT_LINK_UPDATE,

    /**
     * 短链更新 C端
     */
    SHORT_LINK_UPDATE_LINK,

    /**
     * 短链更新 B端
     */
    SHORT_LINK_UPDATE_MAPPING,


    /**
     * 短链删除
     */
    SHORT_LINK_DEL,


    /**
     * 短链删除 C端
     */
    SHORT_LINK_DEL_LINK,

    /**
     * 短链删除 B端
     */
    SHORT_LINK_DEL_MAPPING,

    /**
     * 新建商品订单
     */
    ORDER_NEW,

    /**
     * 订单支付
     */
    ORDER_PAY,

    /**
     * 免费流量包发放
     */
    TRAFFIC_FREE_INIT,

    /**
     * 使用流量包
     */
    TRAFFIC_USED

}
