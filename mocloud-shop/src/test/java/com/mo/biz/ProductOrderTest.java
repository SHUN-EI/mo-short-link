package com.mo.biz;

import com.mo.ShopApplication;
import com.mo.manager.ProductOrderManager;
import com.mo.model.ProductOrderDO;
import com.mo.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * Created by mo on 2022/3/1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class)
@Slf4j
public class ProductOrderTest {

    @Autowired
    private ProductOrderManager productOrderManager;

    @Test
    public void testAddProductOrder() {

        ProductOrderDO productOrderDO = ProductOrderDO.builder()
                .outTradeNo(CommonUtil.generateUUID())
                .payAmount(new BigDecimal(100))
                .state("NEW")
                .nickname("tony")
                .accountNo(100L)
                .del(0)
                .productId(2L)
                .build();
        productOrderManager.add(productOrderDO);
        log.info("订单新增成功:{}", productOrderDO);
    }


}
