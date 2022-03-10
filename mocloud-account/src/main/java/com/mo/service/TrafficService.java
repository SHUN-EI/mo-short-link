package com.mo.service;

import com.mo.model.EventMessage;

/**
 * Created by mo on 2022/3/10
 */
public interface TrafficService {

    void processTrafficMessage(EventMessage eventMessage);
}
