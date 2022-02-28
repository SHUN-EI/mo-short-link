package com.mo.controller;

import com.mo.request.ShortLinkAddRequest;
import com.mo.request.ShortLinkDeleteRequest;
import com.mo.request.ShortLinkPageRequest;
import com.mo.request.ShortLinkUpdateRequest;
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

import java.util.Map;

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

    @ApiOperation("分页查找短链-B端")
    @PostMapping("/pageByGroupId")
    public JsonData pageByGroupId(@ApiParam("短链分页请求对象") @RequestBody ShortLinkPageRequest request) {
        Map<String, Object> result = shortLinkService.pageByGroupId(request);

        return JsonData.buildSuccess(result);
    }

    @ApiOperation("删除短链")
    @PostMapping("/delete")
    public JsonData delete(@ApiParam("短链删除请求对象") @RequestBody ShortLinkDeleteRequest request) {
        JsonData jsonData = shortLinkService.delete(request);
        return jsonData;
    }

    @ApiOperation("更新短链")
    @PostMapping("/update")
    public JsonData update(@ApiParam("短链更新请求对象") @RequestBody ShortLinkUpdateRequest request) {
        JsonData jsonData = shortLinkService.update(request);
        return jsonData;
    }

}
