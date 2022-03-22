package com.mo.manager;

import com.mo.model.TrafficDO;
import com.mo.model.TrafficTaskDO;

/**
 * Created by mo on 2022/3/22
 */
public interface TrafficTaskManager {

    Integer add(TrafficTaskDO trafficTaskDO);

    TrafficTaskDO findByIdAndAccountNo(Long id,Long accountNo);

    Integer updateTaskState(Long id,Long accountNo,String newState,String oldState);

    Integer delete(Long id,Long accountNo);
}
