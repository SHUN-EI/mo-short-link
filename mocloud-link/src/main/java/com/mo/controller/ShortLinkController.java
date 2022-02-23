package com.mo.controller;

import com.mo.request.ShortLinkAddRequest;
import com.mo.service.ShortLinkService;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mo on 2022/2/23
 */
@Api(tags = "短链模块")
@RestController
@RequestMapping("/api/link/v1")
public class ShortLinkController {

    @Autowired
    private ShortLinkService shortLinkService;

    @ApiOperation("创建短链")
    @PostMapping("/add")
    public JsonData createShortLink(@ApiParam("短链新增请求对象") @RequestBody ShortLinkAddRequest request) {

        JsonData jsonData = shortLinkService.createShortLink(request);
        return jsonData;
    }

}
