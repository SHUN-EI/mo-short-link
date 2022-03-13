package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.manager.TrafficManager;
import com.mo.mapper.TrafficMapper;
import com.mo.model.TrafficDO;
import com.mo.request.TrafficPageRequest;
import com.mo.utils.TimeUtil;
import com.mo.vo.TrafficVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mo on 2022/3/10
 */
@Component
public class TrafficManagerImpl implements TrafficManager {

    @Autowired
    private TrafficMapper trafficMapper;

    /**
     * 物理删除过期流量包
     *
     * @param accountNo
     * @return
     */
    @Override
    public Integer deleteExpireTraffic(Long accountNo) {

        int result = trafficMapper.delete(new QueryWrapper<TrafficDO>()
                .eq("account_no", accountNo)
                //体验时间小于等于当前时间
                .le("expired_date", new Date()));

        return result;
    }

    @Override
    public Integer add(TrafficDO trafficDO) {

        return trafficMapper.insert(trafficDO);
    }

    @Override
    public TrafficDO findByIdAndAccountNo(Long trafficId, Long accountNo) {
        return trafficMapper.selectOne(new QueryWrapper<TrafficDO>()
                .eq("account_no", accountNo)
                .eq("id", trafficId));
    }

    /**
     * 给某个流量包增加天使用次数
     *
     * @param currentTrafficId
     * @param accountNo
     * @param dayUsedTimes
     * @return
     */
    @Override
    public Integer addDayUsedTimes(Long currentTrafficId, Long accountNo, Integer dayUsedTimes) {
        return trafficMapper.update(null, new UpdateWrapper<TrafficDO>()
                .eq("account_no", accountNo)
                .eq("id", currentTrafficId)
                .set("day_used", dayUsedTimes));
    }

    @Override
    public Map<String, Object> pageTrafficList(TrafficPageRequest request) {

        Page<TrafficDO> pageInfo = new Page<>(request.getPage(), request.getSize());
        //当前日期
        String today = TimeUtil.format(new Date(), "yyyy-MM-dd");

        //筛选大于过期日期的流量包
        Page<TrafficDO> trafficDOPage = trafficMapper.selectPage(pageInfo, new QueryWrapper<TrafficDO>()
                .eq("account_no", request.getAccountNo())
                .ge("expired_date", today)
                .orderByDesc("create_time"));

        Map<String, Object> resultMap = new HashMap<>(3);

        List<TrafficDO> trafficDOList = trafficDOPage.getRecords();
        List<TrafficVO> trafficVOList = trafficDOList.stream().map(obj -> beanProcess(obj)).collect(Collectors.toList());

        resultMap.put("total_record", trafficDOPage.getTotal());
        resultMap.put("total_page", trafficDOPage.getPages());
        resultMap.put("current_data", trafficVOList);

        return resultMap;
    }

    private TrafficVO beanProcess(TrafficDO trafficDO) {

        TrafficVO trafficVO = new TrafficVO();
        BeanUtils.copyProperties(trafficDO, trafficVO);
        return trafficVO;
    }
}
