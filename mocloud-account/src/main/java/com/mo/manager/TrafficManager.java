package com.mo.manager;

import com.mo.model.TrafficDO;
import com.mo.request.TrafficPageRequest;

import java.util.List;
import java.util.Map;

/**
 * Created by mo on 2022/3/10
 */
public interface TrafficManager {

    Integer add(TrafficDO trafficDO);

    TrafficDO findByIdAndAccountNo(Long trafficId, Long accountNo);

    Map<String, Object> pageTrafficList(TrafficPageRequest request);

    Integer deleteExpireTraffic(Long accountNo);

    List<TrafficDO> selectAvailableTraffics(Long accountNo);

    Integer addDayUsedTimes(Long accountNo, Long trafficId, Integer dayUsedTimes);

    Integer releaseUsedTimes(Long accountNo, Long trafficId, Integer dayUsedTimes);

    Integer batchUpdateUsedTimes(Long accountNo, List<Long> unUpdatedTrafficIds);

}
