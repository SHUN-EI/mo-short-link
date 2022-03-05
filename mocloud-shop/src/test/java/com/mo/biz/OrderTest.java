package com.mo.biz;

import com.mo.ShopApplication;
import com.mo.enums.OrderCodeEnum;
import com.mo.utils.DateUtil;
import com.mo.utils.OrderCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by mo on 2022/3/2
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class)
@Slf4j
public class OrderTest {

    @Autowired
    private OrderCodeGenerateUtil orderCodeGenerateUtil;

    @Test
    public void testOrderCodeGenerate() {
        String orderCode = orderCodeGenerateUtil.generateOrderCode(OrderCodeEnum.XD);
        String refundOrderCode = orderCodeGenerateUtil.generateOrderCode(OrderCodeEnum.TK);
        log.info("自动生成的订单号:{}", orderCode);
        log.info("自动生成的退款单号:{}", refundOrderCode);
        //自动生成的订单号:XD220302000000006183
        //自动生成的订单号:XD220302000000011160
    }

    @Test
    public void testCurrentTime() {
        String currentTime = DateUtil.getCurrentTime(OrderCodeEnum.XD.getDatePattern());
        log.info("当前日期为:{}", currentTime);
    }
}
