package com.mo.controller;

import com.mo.request.ShortLinkAddRequest;
import com.mo.request.ShortLinkDeleteRequest;
import com.mo.request.ShortLinkPageRequest;
import com.mo.request.ShortLinkUpdateRequest;
import com.mo.service.ShortLinkService;
import com.mo.utils.JsonData;
import com.mo.vo.ShortLinkVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    @Value("${rpc.token}")
    private String rpcToekn;

    @ApiOperation("检查短链是否存在")
    @GetMapping("/checkShortLinkExists")
    public JsonData checkShortLinkExists(@ApiParam("短链码") @RequestParam("shortLinkCode") String shortLinkCode, HttpServletRequest servletRequest) {

        //获取请求接口的鉴权token
        String requestToken = servletRequest.getHeader("rpc-token");

        if (requestToken.equalsIgnoreCase(rpcToekn)) {
            ShortLinkVO shortLinkVO = shortLinkService.parseShortLinkCode(shortLinkCode);
            return shortLinkVO == null ? JsonData.buildError("短链不存在")
                    : JsonData.buildSuccess(shortLinkVO);
        } else {
            //非法访问
            return JsonData.buildError(HttpStatus.FORBIDDEN.name());
        }
    }

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
