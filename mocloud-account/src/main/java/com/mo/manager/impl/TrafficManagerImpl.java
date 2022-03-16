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
     * 批量更新流量包使用次数为0
     *
     * @param accountNo
     * @param unUpdatedTrafficIds
     * @return
     */
    @Override
    public Integer batchUpdateUsedTimes(Long accountNo, List<Long> unUpdatedTrafficIds) {
        int rows = trafficMapper.update(null, new UpdateWrapper<TrafficDO>()
                .eq("account_no", accountNo)
                .in("id", unUpdatedTrafficIds)
                .set("day_used", 0));

        return rows;
    }

    /**
     * 恢复流量包的当天使用次数
     * 流量包每天都有一定的使用次数，如每天5次，每天10次等
     *
     * @param accountNo
     * @param trafficId
     * @param dayUsedTimes
     * @return
     */
    @Override
    public Integer releaseUsedTimes(Long accountNo, Long trafficId, Integer dayUsedTimes) {
        return trafficMapper.releaseUsedTimes(accountNo, trafficId, dayUsedTimes);
    }

    /**
     * 查找可用的短链流量包(未过期),包括免费流量包
     * （不一定可用，可能超过次数）
     * select * from traffic where account_no =111
     * and (expired_date >= ? OR out_trade_no=free_init )
     *
     * @param accountNo
     * @return
     */
    @Override
    public List<TrafficDO> selectAvailableTraffics(Long accountNo) {

        String today = TimeUtil.format(new Date(), "yyyy-MM-dd");

        QueryWrapper<TrafficDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account_no", accountNo);
        queryWrapper.and(wrapper -> wrapper.ge("expired_date", today)
                .or().eq("out_trade_no", "free_init"));

        List<TrafficDO> trafficDOList = trafficMapper.selectList(queryWrapper);

        return trafficDOList;
    }

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
     * @param accountNo
     * @param trafficId
     * @param dayUsedTimes
     * @return
     */
    @Override
    public Integer addDayUsedTimes(Long accountNo, Long trafficId, Integer dayUsedTimes) {

        return trafficMapper.addDayUsedTimes(accountNo, trafficId, dayUsedTimes);
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
