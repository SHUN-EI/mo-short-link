package com.mo.service;

import com.mo.request.LinkGroupAddRequest;
import com.mo.request.LinkGroupDetailRequest;
import com.mo.utils.JsonData;

/**
 * Created by mo on 2022/2/17
 */
public interface LinkGroupService {
    JsonData add(LinkGroupAddRequest request);

    JsonData delete(LinkGroupDetailRequest request);
}
