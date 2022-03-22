package com.mo.feign;

import com.mo.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by mo on 2022/3/22
 */
@FeignClient(name = "mocloud-link")
public interface ShortLinkFeignService {

    /**
     * 检查短链是否存在
     *
     * @param shortLinkCode
     * @return
     */
    @GetMapping(value = "/api/link/v1/checkShortLinkExists", headers = {"rpc-token=${rpc.token}"})
    JsonData checkShortLinkExists(@RequestParam("shortLinkCode") String shortLinkCode);
}
