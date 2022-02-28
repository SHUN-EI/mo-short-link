package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
    public Integer update(ShortLinkDO shortLinkDO) {

        int rows = shortLinkMapper.update(null, new UpdateWrapper<ShortLinkDO>()
                .eq("code", shortLinkDO.getCode())
                .eq("del", 0)
                .eq("account_no", shortLinkDO.getAccountNo())
                .set("title", shortLinkDO.getTitle())
                .set("domain", shortLinkDO.getDomain()));

        return rows;
    }

    @Override
    public Integer addShortLink(ShortLinkDO shortLinkDO) {

        return shortLinkMapper.insert(shortLinkDO);
    }

    @Override
    public ShortLinkDO findByShortLinCode(String shortLinkCode) {

        return shortLinkMapper.selectOne(new QueryWrapper<ShortLinkDO>().eq("code", shortLinkCode).eq("del", 0));
    }

    /**
     * 逻辑删除
     *
     * @param shortLinkDO
     * @return
     */
    @Override
    public Integer del(ShortLinkDO shortLinkDO) {

        return shortLinkMapper.update(null, new UpdateWrapper<ShortLinkDO>()
                .eq("code", shortLinkDO.getCode())
                .eq("account_no", shortLinkDO.getAccountNo())
                .set("del", 1));
    }
}
