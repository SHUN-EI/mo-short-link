package com.mo.manager;

import com.mo.model.AccountDO;

import java.util.List;

/**
 * Created by mo on 2022/2/14
 */
public interface AccountManager {

    Integer insert(AccountDO accountDO);

    List<AccountDO> findByPhone(String phone);
}
