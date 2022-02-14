package com.mo.manager;

import com.mo.mapper.AccountMapper;
import com.mo.model.AccountDO;
import groovyjarjarpicocli.CommandLine;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2022/2/14
 */
@Component
public class AccountManagerImpl implements AccountManager {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Integer insert(AccountDO accountDO) {
        return accountMapper.insert(accountDO);
    }
}
