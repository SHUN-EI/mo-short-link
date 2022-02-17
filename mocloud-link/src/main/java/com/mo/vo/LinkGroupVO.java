package com.mo.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by mo on 2022/2/17
 */
@Data
public class LinkGroupVO {

    private Long id;

    /**
     * 组名
     */
    private String title;

    /**
     * 账号唯一编号
     */
    private Long accountNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
