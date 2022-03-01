package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.manager.ProductOrderManager;
import com.mo.mapper.ProductOrderMapper;
import com.mo.model.ProductDO;
import com.mo.model.ProductOrderDO;
import com.mo.vo.ProductOrderVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mo on 2022/3/1
 */
@Component
public class ProductOrderManagerImpl implements ProductOrderManager {

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Override
    public Integer add(ProductOrderDO productOrderDO) {
        return productOrderMapper.insert(productOrderDO);
    }

    @Override
    public ProductOrderDO findByOutTradeNoAndAccountNo(String outTradeNo, Long accountNo) {

        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>()
                .eq("out_trade_no", outTradeNo)
                .eq("account_no", accountNo)
                .eq("del", 0));
        return productOrderDO;
    }

    @Override
    public Integer updateOrderPayState(String outTradeNo, Long accountNo, String newState, String oldState) {

        int rows = productOrderMapper.update(null, new UpdateWrapper<ProductOrderDO>()
                .eq("out_trade_no", outTradeNo)
                .eq("account_no", accountNo)
                .eq("state", oldState)
                .set("state", newState));
        return rows;
    }

    @Override
    public Map<String, Object> page(int page, int size, Long accountNo, String state) {

        Page<ProductOrderDO> pageInfo = new Page<>(page, size);
        Page<ProductOrderDO> productOrderDOPage;

        if (StringUtils.isBlank(state)) {
            productOrderDOPage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>()
                    .eq("account_no", accountNo)
                    .eq("del", 0));
        } else {
            //根据支付状态筛选
            productOrderDOPage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>()
                    .eq("account_no", accountNo)
                    .eq("state", state)
                    .eq("del", 0));
        }

        List<ProductOrderDO> productOrderDOList = productOrderDOPage.getRecords();
        List<ProductOrderVO> productOrderVOList = productOrderDOList.stream().map(obj -> {
            ProductOrderVO productOrderVO = new ProductOrderVO();
            BeanUtils.copyProperties(obj, productOrderVO);
            return productOrderVO;
        }).collect(Collectors.toList());

        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record",productOrderDOPage.getTotal());
        pageMap.put("total_page",productOrderDOPage.getPages());
        pageMap.put("current_data",productOrderVOList);

        return pageMap;
    }

    /**
     * 逻辑删除
     *
     * @param productOrderId
     * @param accountNo
     * @return
     */
    @Override
    public Integer delete(Long productOrderId, Long accountNo) {
        int rows = productOrderMapper.update(null, new UpdateWrapper<ProductOrderDO>()
                .eq("id", productOrderId)
                .eq("account_no", accountNo)
                .set("del", 1));

        return rows;
    }
}
