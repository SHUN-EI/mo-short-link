package com.mo.controller;

import com.alibaba.fastjson.JSON;
import com.mo.component.FileService;
import com.mo.enums.BizCodeEnum;
import com.mo.request.AccountLoginRequest;
import com.mo.request.AccountRegisterRequest;
import com.mo.service.AccountService;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by mo on 2022/2/14
 */
@Api(tags = "账号模块")
@RestController
@RequestMapping("/api/account/v1")
public class AccountController {

    @Autowired
    private FileService fileService;
    @Autowired
    private AccountService accountService;


    @ApiOperation("账号头像上传")
    @PostMapping("/upload")
    public JsonData uploadAccountImg(@ApiParam(value = "文件上传", required = true)
                                     @RequestPart("file") MultipartFile file) {

        String result = fileService.uploadUserImg(file);
        return result != null ? JsonData.buildSuccess(result) : JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public JsonData register(@ApiParam("用户注册对象") @RequestBody AccountRegisterRequest request) {

        JsonData jsonData = accountService.register(request);
        return jsonData.buildSuccess(jsonData);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public JsonData login(@ApiParam("用户登录对象") @RequestBody AccountLoginRequest request) {
        JsonData jsonData = accountService.login(request);
        return jsonData.buildSuccess(jsonData);
    }

}
