package com.mo.manager;

import com.mo.model.TrafficDO;
import com.mo.request.TrafficPageRequest;

import java.util.Map;

/**
 * Created by mo on 2022/3/10
 */
public interface TrafficManager {

    Integer add(TrafficDO trafficDO);

    TrafficDO findByIdAndAccountNo(Long trafficId,Long accountNo);

    Integer addDayUsedTimes(Long currentTrafficId, Long accountNo, Integer dayUsedTimes);

    Map<String, Object> pageTrafficList(TrafficPageRequest request);

    Integer deleteExpireTraffic(Long accountNo);
}
