package com.mo.biz;

import com.mo.AccountApplication;
import com.mo.manager.TrafficManager;
import com.mo.mapper.TrafficMapper;
import com.mo.model.TrafficDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * Created by mo on 2022/2/16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@Slf4j
public class TrafficTest {

    @Autowired
    private TrafficMapper trafficMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private TrafficManager trafficManager;


    @Test
    public void testDeleteExpiredTraffic() {
        Integer rows = trafficManager.deleteExpireTraffic(Long.valueOf("701142527029280768"));
        log.info("删除过期流量包行数：rows={}",rows);
    }

    @Test
    public void testSendMsg() {
        rabbitTemplate.convertAndSend("order.event.exchange", "order.update.traffic.routing.key", "this is traffic message");

    }

    /**
     * 测试流量包表 的水平分表情况
     */
    @Test
    public void testSaveTraffic() {

        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            TrafficDO trafficDO = new TrafficDO();
            trafficDO.setAccountNo(Long.valueOf(random.nextInt(1000)));
            trafficMapper.insert(trafficDO);
        }
    }
}
