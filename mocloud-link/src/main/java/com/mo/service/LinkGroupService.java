package com.mo.service;

import com.mo.request.LinkGroupAddRequest;
import com.mo.request.LinkGroupDetailRequest;
import com.mo.request.LinkGroupUpdateRequest;
import com.mo.utils.JsonData;
import com.mo.vo.LinkGroupVO;

import java.util.List;

/**
 * Created by mo on 2022/2/17
 */
public interface LinkGroupService {
    JsonData add(LinkGroupAddRequest request);

    JsonData delete(LinkGroupDetailRequest request);

    JsonData detail(LinkGroupDetailRequest request);

    List<LinkGroupVO> findAccountAllLinkGroup();

    JsonData update(LinkGroupUpdateRequest request);
}
