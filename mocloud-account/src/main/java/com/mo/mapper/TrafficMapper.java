package com.mo.mapper;

import com.mo.model.TrafficDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 流量包表 Mapper 接口
 * </p>
 *
 * @author mo
 * @since 2022-02-08
 */
public interface TrafficMapper extends BaseMapper<TrafficDO> {

    /**
     * 给某个流量包增加天使用次数
     * @param accountNo
     * @param trafficId
     * @param dayUsedTimes
     * @return
     */
    Integer addDayUsedTimes(@Param("accountNo") Long accountNo,
                            @Param("trafficId") Long trafficId,
                            @Param("dayUsedTimes") Integer dayUsedTimes);

    /**
     * 恢复流量包的当天使用次数
     * @param accountNo
     * @param trafficId
     * @param dayUsedTimes
     * @return
     */
    Integer releaseUsedTimes(@Param("accountNo") Long accountNo,
                             @Param("trafficId") Long trafficId,
                             @Param("dayUsedTimes") Integer dayUsedTimes);
}
