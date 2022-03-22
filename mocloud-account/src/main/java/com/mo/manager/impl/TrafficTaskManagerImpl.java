package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mo.manager.TrafficTaskManager;
import com.mo.mapper.TrafficTaskMapper;
import com.mo.model.TrafficTaskDO;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2022/3/22
 */
@Component
public class TrafficTaskManagerImpl implements TrafficTaskManager {

    @Autowired
    private TrafficTaskMapper trafficTaskMapper;

    @Override
    public Integer updateTaskState(Long id, Long accountNo, String newState, String oldState) {

        int rows = trafficTaskMapper.update(null, new UpdateWrapper<TrafficTaskDO>()
                .eq("id", id)
                .eq("account_no", accountNo)
                .eq("lock_state", oldState)
                .set("lock_state", newState));
        return rows;
    }

    @Override
    public Integer add(TrafficTaskDO trafficTaskDO) {
        return trafficTaskMapper.insert(trafficTaskDO);
    }

    @Override
    public TrafficTaskDO findByIdAndAccountNo(Long id, Long accountNo) {
        return trafficTaskMapper.selectOne(new QueryWrapper<TrafficTaskDO>()
                .eq("id", id)
                .eq("account_no", accountNo));
    }

    @Override
    public Integer delete(Long id, Long accountNo) {
        return trafficTaskMapper.delete(new QueryWrapper<TrafficTaskDO>()
                .eq("id", id)
                .eq("account_no", accountNo));
    }
}
