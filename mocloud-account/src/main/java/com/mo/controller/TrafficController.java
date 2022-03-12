package com.mo.controller;

import com.mo.request.TrafficPageRequest;
import com.mo.service.TrafficService;
import com.mo.utils.JsonData;
import com.mo.vo.TrafficVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by mo on 2022/3/12
 */
@Api(tags = "流量包模块")
@RestController
@RequestMapping("/api/traffic/v1")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;


    @ApiOperation("查找某个流量包详情")
    @GetMapping("/detail/{trafficId}")
    public JsonData detail(@PathVariable("trafficId") Long trafficId) {
        TrafficVO trafficVO = trafficService.detail(trafficId);
        return JsonData.buildSuccess(trafficVO);
    }

    @ApiOperation("分页查询流量包列表")
    @PostMapping("/pageTrafficList")
    public JsonData pageTrafficList(@RequestBody TrafficPageRequest request) {
        Map<String, Object> pageMap = trafficService.pageTrafficList(request);
        return JsonData.buildSuccess(pageMap);
    }
}
