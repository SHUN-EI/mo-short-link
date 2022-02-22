package com.mo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by mo on 2022/2/23
 * RabbitMQ 消息对象
 */
@Data
public class EventMessage implements Serializable {

    /**
     * 消息队列的消息id
     */
    private String messageId;


    /**
     * 事件类型
     */
    private String eventMessageType;


    /**
     * 业务id
     */
    private String bizId;


    /**
     * 账号
     */
    private Long accountNo;


    /**
     * 消息体
     */
    private String content;

    /**
     * 备注
     */
    private String remark;
}
