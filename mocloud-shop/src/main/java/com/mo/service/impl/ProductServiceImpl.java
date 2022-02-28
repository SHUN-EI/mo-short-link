package com.mo.service.impl;

import com.mo.manager.ProductManager;
import com.mo.model.ProductDO;
import com.mo.request.ProductPageRequest;
import com.mo.service.ProductService;
import com.mo.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by mo on 2022/2/28
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductManager productManager;


    /**
     * 分页查询商品列表
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> pageProductList(ProductPageRequest request) {

        Map<String, Object> resultMap = productManager.pageProductList(request);

        return resultMap;
    }

    /**
     * 商品详情
     *
     * @param productId
     * @return
     */
    @Override
    public ProductVO findById(Long productId) {

        ProductDO productDO = productManager.findById(productId);
        ProductVO productVO = beanProcess(productDO);
        return productVO;
    }

    private ProductVO beanProcess(ProductDO productDO) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO, productVO);
        return productVO;
    }
}
