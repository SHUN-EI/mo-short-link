package com.mo.manager;

import com.mo.model.ProductDO;
import com.mo.request.ProductPageRequest;

import java.util.Map;

/**
 * Created by mo on 2022/2/28
 */
public interface ProductManager {
    ProductDO findById(Long productId);

    Map<String, Object> pageProductList(ProductPageRequest request);
}
