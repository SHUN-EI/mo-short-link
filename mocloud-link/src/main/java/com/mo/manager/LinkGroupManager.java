package com.mo.manager;

import com.mo.model.LinkGroupDO;

import java.util.List;

/**
 * Created by mo on 2022/2/17
 */
public interface LinkGroupManager {

    int add(LinkGroupDO linkGroupDO);

    int del(Long groupId, Long accountNo);

    LinkGroupDO detail(Long id, Long accountNo);

    List<LinkGroupDO> findAccountAllLinkGroup(Long accountNo);

    int update(LinkGroupDO linkGroupDO);
}
