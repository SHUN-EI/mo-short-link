package com.mo.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 短链表
 * </p>
 *
 * @author mo
 * @since 2022-02-17
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("short_link")
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkDO implements Serializable {

    private static final long serialVersionUID = 1L;

      //@TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组编号
     */
    private Long groupId;

    /**
     * 短链标题
     */
    private String title;

    /**
     * 原始url地址
     */
    private String originalUrl;

    /**
     * 短链域名
     */
    private String domain;

    /**
     * 短链压缩码
     */
    private String code;

    /**
     * 长链的md5码，方便查找
     */
    private String sign;

    /**
     * 过期时间，长久就是-1
     */
    private Date expired;

    /**
     * 账号唯一编号
     */
    private Long accountNo;

    /**
     * 0是默认，1是删除
     */
    private Integer del;

    /**
     * 状态，lock是锁定不可用，active是可用
     */
    private String state;

    /**
     * 链接产品层级：FIRST 免费青铜、SECOND黄金、THIRD钻石
     */
    private String linkType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
