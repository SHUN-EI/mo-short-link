package com.mo.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 短链分组表
 * </p>
 *
 * @author mo
 * @since 2022-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("link_group")
public class LinkGroupDO implements Serializable {

    private static final long serialVersionUID = 1L;

      //@TableId(value = "id", type = IdType.AUTO)
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
