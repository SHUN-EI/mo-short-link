package com.mo.biz;

import com.mo.ShopApplication;
import com.mo.config.PayBeanConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.PrivateKey;

/**
 * Created by mo on 2022/3/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class)
@Slf4j
public class WechatPayTest {

    @Autowired
    private PayBeanConfig payBeanConfig;

    @Test
    public void testLoadPrivateKey() throws IOException {

        PrivateKey privateKey = payBeanConfig.getPrivateKey();
        log.info("微信支付的私钥:{}",privateKey.toString());
        log.info("微信支付的私钥的算法:{}",privateKey.getAlgorithm());
    }

}
