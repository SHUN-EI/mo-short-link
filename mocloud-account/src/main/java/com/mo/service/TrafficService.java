package com.mo.service;

import com.mo.model.EventMessage;
import com.mo.request.TrafficPageRequest;
import com.mo.request.TrafficUseRequest;
import com.mo.utils.JsonData;
import com.mo.vo.TrafficVO;

import java.util.Map;

/**
 * Created by mo on 2022/3/10
 */
public interface TrafficService {

    void processTrafficMessage(EventMessage eventMessage);

    Map<String, Object> pageTrafficList(TrafficPageRequest request);

    TrafficVO detail(Long trafficId);

    Boolean deleteExpireTraffic();

    JsonData reduce(TrafficUseRequest request);
}
