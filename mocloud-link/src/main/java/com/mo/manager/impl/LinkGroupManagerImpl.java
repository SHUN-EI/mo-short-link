package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.manager.LinkGroupManager;
import com.mo.mapper.LinkGroupMapper;
import com.mo.model.LinkGroupDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2022/2/17
 */
@Component
public class LinkGroupManagerImpl implements LinkGroupManager {

    @Autowired
    private LinkGroupMapper linkGroupMapper;

    @Override
    public int del(Long groupId, Long accountNo) {

        return linkGroupMapper.delete(new QueryWrapper<LinkGroupDO>().eq("id",groupId).eq("account_no",accountNo));
    }

    @Override
    public int add(LinkGroupDO linkGroupDO) {
        return linkGroupMapper.insert(linkGroupDO);
    }
}
