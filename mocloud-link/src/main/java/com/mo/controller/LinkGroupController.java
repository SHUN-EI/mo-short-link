package com.mo.controller;

import com.mo.request.LinkGroupAddRequest;
import com.mo.request.LinkGroupDetailRequest;
import com.mo.request.LinkGroupUpdateRequest;
import com.mo.service.LinkGroupService;
import com.mo.utils.JsonData;
import com.mo.vo.LinkGroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mo on 2022/2/17
 */
@Api(tags = "短链分组模块")
@RestController
@RequestMapping("/api/group/v1")
public class LinkGroupController {

    @Autowired
    private LinkGroupService linkGroupService;

    @ApiOperation("更新短链分组")
    @PostMapping("/update")
    public JsonData update(@ApiParam("短链分组更新请求对象") @RequestBody LinkGroupUpdateRequest request) {
        JsonData jsonData = linkGroupService.update(request);
        return jsonData;
    }

    @ApiOperation("查看账号下所有的短链分组")
    @PostMapping("/list")
    public JsonData findAccountAllLinkGroup() {
        List<LinkGroupVO> list = linkGroupService.findAccountAllLinkGroup();
        return JsonData.buildSuccess(list);
    }

    @ApiOperation("查看短链分组详情")
    @PostMapping("/detail")
    public JsonData detail(@ApiParam("短链分组详情/删除请求对象") @RequestBody LinkGroupDetailRequest request) {
        JsonData jsonData = linkGroupService.detail(request);
        return jsonData;
    }

    @ApiOperation("删除短链分组")
    @PostMapping("/delete")
    public JsonData delete(@ApiParam("短链分组详情/删除请求对象") @RequestBody LinkGroupDetailRequest request) {
        JsonData jsonData = linkGroupService.delete(request);
        return jsonData;
    }

    @ApiOperation("添加短链分组")
    @PostMapping("/add")
    public JsonData add(@ApiParam("添加短链分组请求对象") @RequestBody LinkGroupAddRequest request) {

        JsonData jsonData = linkGroupService.add(request);
        return jsonData;
    }


}
