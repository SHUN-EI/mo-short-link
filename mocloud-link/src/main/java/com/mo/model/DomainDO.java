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
 * 短链域名表
 * </p>
 *
 * @author mo
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("domain")
public class DomainDO implements Serializable {

    private static final long serialVersionUID = 1L;

      //@TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户自己绑定的域名
     */
    private Long accountNo;

    /**
     * 域名类型，自建custom, 官方offical
     */
    private String domainType;

    private String value;

    /**
     * 0是默认，1是禁用
     */
    private Integer del;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
