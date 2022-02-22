package com.mo.manager;

import com.mo.model.LinkGroupDO;

import java.util.List;

/**
 * Created by mo on 2022/2/17
 */
public interface LinkGroupManager {

    Integer add(LinkGroupDO linkGroupDO);

    Integer del(Long groupId, Long accountNo);

    LinkGroupDO detail(Long id, Long accountNo);

    List<LinkGroupDO> findAccountAllLinkGroup(Long accountNo);

    Integer update(LinkGroupDO linkGroupDO);
}
