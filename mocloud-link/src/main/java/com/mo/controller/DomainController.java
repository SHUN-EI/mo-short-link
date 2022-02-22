package com.mo.controller;

import com.mo.service.DomainService;
import com.mo.utils.JsonData;
import com.mo.vo.DomainVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mo on 2022/2/22
 */
@Api(tags = "短链域名模块")
@RestController
@RequestMapping("/api/domain/v1")
public class DomainController {

    @Autowired
    private DomainService domainService;


    @ApiOperation("查看账号下所有的域名")
    @GetMapping("/list")
    public JsonData listAll() {

        List<DomainVO> list = domainService.listAll();
        return JsonData.buildSuccess(list);
    }
}
