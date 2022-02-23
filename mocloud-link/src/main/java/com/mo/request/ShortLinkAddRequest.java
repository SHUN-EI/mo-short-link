package com.mo.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * Created by mo on 2022/2/23
 */
@ApiModel(value = "短链新增请求对象", description = "短链新增请求对象")
@Data
public class ShortLinkAddRequest {

    /**
     * 组
     */
    private Long groupId;

    /**
     * 短链标题
     */
    private String title;

    /**
     * 原生url
     */
    private String originalUrl;

    /**
     * 域名id
     */
    private Long domainId;

    /**
     * 域名类型
     */
    private String domainType;

    /**
     * 过期时间
     */
    private Date expired;
}
