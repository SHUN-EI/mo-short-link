package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.manager.LinkGroupManager;
import com.mo.mapper.LinkGroupMapper;
import com.mo.model.LinkGroupDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mo on 2022/2/17
 */
@Component
public class LinkGroupManagerImpl implements LinkGroupManager {

    @Autowired
    private LinkGroupMapper linkGroupMapper;

    @Override
    public Integer update(LinkGroupDO linkGroupDO) {

        return linkGroupMapper.update(linkGroupDO, new QueryWrapper<LinkGroupDO>().eq("id", linkGroupDO.getId()).eq("account_no", linkGroupDO.getAccountNo()));
    }

    @Override
    public List<LinkGroupDO> findAccountAllLinkGroup(Long accountNo) {

        return linkGroupMapper.selectList(new QueryWrapper<LinkGroupDO>().eq("account_no", accountNo));
    }

    @Override
    public LinkGroupDO detail(Long id, Long accountNo) {

        return linkGroupMapper.selectOne(new QueryWrapper<LinkGroupDO>().eq("id", id).eq("account_no", accountNo));
    }

    @Override
    public Integer del(Long groupId, Long accountNo) {

        return linkGroupMapper.delete(new QueryWrapper<LinkGroupDO>().eq("id", groupId).eq("account_no", accountNo));
    }

    @Override
    public Integer add(LinkGroupDO linkGroupDO) {
        return linkGroupMapper.insert(linkGroupDO);
    }
}
