package com.mo.feign;

import com.mo.request.TrafficUseRequest;
import com.mo.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by mo on 2022/3/18
 */
@FeignClient(name = "mocloud-account")
public interface TrafficFeignService {

    /**
     * 流量包使用
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/api/traffic/v1/reduce", headers = {"rpc-token=${rpc.token}"})
    JsonData reduceTraffic(@RequestBody TrafficUseRequest request);
}
