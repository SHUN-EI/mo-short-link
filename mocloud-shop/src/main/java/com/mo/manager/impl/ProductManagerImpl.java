package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.manager.ProductManager;
import com.mo.mapper.ProductMapper;
import com.mo.model.ProductDO;
import com.mo.request.ProductPageRequest;
import com.mo.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mo on 2022/2/28
 */
@Component
public class ProductManagerImpl implements ProductManager {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Map<String, Object> pageProductList(ProductPageRequest request) {
        Page<ProductDO> pageInfo = new Page<>(request.getPage(), request.getSize());

        Page<ProductDO> productDOPage = productMapper.selectPage(pageInfo, null);

        HashMap<String, Object> pageMap = new HashMap<>(3);
        //总条数
        pageMap.put("total_record", productDOPage.getTotal());
        //总页数
        pageMap.put("total_page", productDOPage.getPages());
        //组装返回前端的对象
        List<ProductVO> productVOS = productDOPage.getRecords().stream().map(obj -> {
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(obj, productVO);
            return productVO;
        }).collect(Collectors.toList());

        pageMap.put("current_data", productVOS);

        return pageMap;
    }

    @Override
    public ProductDO findById(Long productId) {

        ProductDO productDO = productMapper.selectOne(new QueryWrapper<ProductDO>().eq("id", productId));
        return productDO;
    }
}
