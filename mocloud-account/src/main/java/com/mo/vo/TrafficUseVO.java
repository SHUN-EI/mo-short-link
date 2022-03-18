package com.mo.vo;

import com.mo.model.TrafficDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by mo on 2022/3/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficUseVO {

    /**
     * 天剩余可用总次数 = 总次数 - 已用次数
     */
    private Integer dayTotalLeftTimes;


    /**
     * 当前使用的流量包
     */
    private TrafficDO currentTrafficDO;


    /**
     * 记录没过期，但是今天没更新的流量包id-列表
     */
    private List<Long> unUpdatedTrafficIds;
}
