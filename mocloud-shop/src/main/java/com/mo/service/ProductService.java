package com.mo.service;

import com.mo.request.ProductPageRequest;
import com.mo.vo.ProductVO;

import java.util.Map;

/**
 * Created by mo on 2022/2/28
 */
public interface ProductService {
    ProductVO findById(Long productId);

    Map<String, Object> pageProductList(ProductPageRequest request);
}
