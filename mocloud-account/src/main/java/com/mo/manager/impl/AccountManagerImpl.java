package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.manager.AccountManager;
import com.mo.mapper.AccountMapper;
import com.mo.model.AccountDO;
import groovyjarjarpicocli.CommandLine;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mo on 2022/2/14
 */
@Component
public class AccountManagerImpl implements AccountManager {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public List<AccountDO> findByPhone(String phone) {

        List<AccountDO> accountDOList = accountMapper.selectList(new QueryWrapper<AccountDO>().eq("phone", phone));
        return accountDOList;
    }

    @Override
    public Integer insert(AccountDO accountDO) {
        return accountMapper.insert(accountDO);
    }
}
