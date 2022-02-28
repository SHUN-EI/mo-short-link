package com.mo.controller;

import com.mo.enums.BizCodeEnum;
import com.mo.request.ProductPageRequest;
import com.mo.service.ProductService;
import com.mo.utils.JsonData;
import com.mo.vo.ProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by mo on 2022/2/28
 */
@Api(tags = "商品模块")
@RestController
@RequestMapping("/api/product/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("分页查询商品列表")
    @PostMapping("/pageProductList")
    public JsonData pageProductList(@ApiParam("商品分页请求对象") @RequestBody ProductPageRequest request) {

        Map<String, Object> pageMap = productService.pageProductList(request);
        return JsonData.buildSuccess(pageMap);
    }

    @ApiOperation("商品详情")
    @GetMapping("/detail/{product_id}")
    public JsonData detail(@ApiParam(value = "商品id", required = true)
                           @PathVariable("product_id") Long productId) {

        ProductVO productVO = productService.findById(productId);
        return productVO != null ? JsonData.buildSuccess(productVO) : JsonData.buildResult(BizCodeEnum.PRODUCT_NOT_EXISTS);
    }


}
