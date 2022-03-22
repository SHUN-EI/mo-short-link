package com.mo.biz;

import com.mo.AccountApplication;
import com.mo.constant.TimeConstant;
import com.mo.manager.TrafficManager;
import com.mo.mapper.TrafficMapper;
import com.mo.model.TrafficDO;
import com.mo.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Array;
import java.util.*;

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
    public void testBatchUpdateUsedTimes() {

        Integer rows = trafficManager.batchUpdateUsedTimes(709593930177445888L, Arrays.asList(709593953232068608L));
        log.info("批量更新流量包使用次数为0,{}", rows);
    }

    @Test
    public void testReleaseDayUsedTimes() {
        String useDateStr = TimeUtil.format(new Date(), TimeConstant.DATE_YYYY_MM_DD);
        int rows = trafficManager.releaseUsedTimes(709593930177445888L, 709593953232068608L, 1, useDateStr);
        log.info("恢复流量包的当天使用次数,{}", rows);
    }

    @Test
    public void testAddDayUsedTimes() {
        Integer rows = trafficManager.addDayUsedTimes(709593930177445888L, 709593953232068608L, 1);
        log.info("给某个流量包增加天使用次数,{}", rows);
    }

    @Test
    public void testSelectAvailableTraffics() {
        List<TrafficDO> trafficDOS = trafficManager.selectAvailableTraffics(709593930177445888L);
        trafficDOS.stream().forEach(obj -> log.info("流量包为:{}", obj));
    }


    @Test
    public void testDeleteExpiredTraffic() {
        Integer rows = trafficManager.deleteExpireTraffic(Long.valueOf("701142527029280768"));
        log.info("删除过期流量包行数：rows={}", rows);
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
