package com.mo.service;

import com.mo.request.AccountRegisterRequest;
import com.mo.utils.JsonData;

/**
 * Created by mo on 2022/2/14
 */
public interface AccountService {

    JsonData register(AccountRegisterRequest request);
}
