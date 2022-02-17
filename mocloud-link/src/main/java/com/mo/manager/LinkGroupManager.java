package com.mo.manager;

import com.mo.model.LinkGroupDO;

/**
 * Created by mo on 2022/2/17
 */
public interface LinkGroupManager {

    int add(LinkGroupDO linkGroupDO);

    int del(Long groupId, Long accountNo);
}
