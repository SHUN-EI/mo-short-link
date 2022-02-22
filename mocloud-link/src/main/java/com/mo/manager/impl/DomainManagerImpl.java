package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.enums.DomainTypeEnum;
import com.mo.manager.DomainManager;
import com.mo.mapper.DomainMapper;
import com.mo.model.DomainDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mo on 2022/2/22
 */
@Component
public class DomainManagerImpl implements DomainManager {

    @Autowired
    private DomainMapper domainMapper;

    @Override
    public DomainDO findById(Long id, Long accountNO) {
        return domainMapper.selectOne(new QueryWrapper<DomainDO>().eq("id", id)
                .eq("account_no", accountNO));
    }

    @Override
    public DomainDO findByDomainTypeAndID(Long id, DomainTypeEnum domainTypeEnum) {
        return domainMapper.selectOne(new QueryWrapper<DomainDO>().eq("id", id)
                .eq("domain_type", domainTypeEnum.name()));
    }

    @Override
    public Integer add(DomainDO domainDO) {
        return domainMapper.insert(domainDO);
    }

    @Override
    public List<DomainDO> listOfficialDomain() {
        return domainMapper.selectList(new QueryWrapper<DomainDO>().eq("domain_type", DomainTypeEnum.OFFICIAL.name()));
    }

    @Override
    public List<DomainDO> listCustomDomain(Long accountNo) {
        return domainMapper.selectList(new QueryWrapper<DomainDO>().eq("domain_type", DomainTypeEnum.CUSTOM.name())
                .eq("account_no", accountNo));
    }
}
