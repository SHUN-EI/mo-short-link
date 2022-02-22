package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.manager.ShortLinkManager;
import com.mo.mapper.ShortLinkMapper;
import com.mo.model.ShortLinkDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2022/2/19
 */
@Component
public class ShortLinkManagerImpl implements ShortLinkManager {

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Override
    public int addShortLink(ShortLinkDO shortLinkDO) {

        return shortLinkMapper.insert(shortLinkDO);
    }

    @Override
    public ShortLinkDO findByShortLinCode(String shortLinkCode) {

        return shortLinkMapper.selectOne(new QueryWrapper<ShortLinkDO>().eq("code", shortLinkCode));
    }

    /**
     * 逻辑删除
     *
     * @param shortLinkCode
     * @param accountNo
     * @return
     */
    @Override
    public int del(String shortLinkCode, Long accountNo) {

        ShortLinkDO shortLinkDO = new ShortLinkDO();
        shortLinkDO.setDel(1);

        return shortLinkMapper.update(shortLinkDO, new QueryWrapper<ShortLinkDO>().eq("code", shortLinkCode).eq("account_no", accountNo));
    }
}