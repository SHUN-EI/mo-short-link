package com.mo.service.impl;

import com.mo.enums.AuthTypeEnum;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.manager.AccountManager;
import com.mo.model.AccountDO;
import com.mo.request.AccountRegisterRequest;
import com.mo.service.AccountService;
import com.mo.service.NotifyService;
import com.mo.utils.CommonUtil;
import com.mo.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mo on 2022/2/14
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private NotifyService notifyService;
    @Autowired
    private AccountManager accountManager;

    /**
     * 用户注册
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData register(AccountRegisterRequest request) {

        boolean checkCode = false;
        //验证码-验证
        if (StringUtils.isNotBlank(request.getTo())) {
            checkCode = notifyService.checkCode(SendCodeEnum.USER_REGISTER, request.getTo(), request.getCode());
        }

        //取反
        if (!checkCode) {
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }

        //插入数据库
        AccountDO accountDO = new AccountDO();
        BeanUtils.copyProperties(request, accountDO);
        accountDO.setAuth(AuthTypeEnum.DEFAULT.name());

        //生成唯一的账号  TODO
        accountDO.setAccountNo(CommonUtil.getCurrentTimestamp());

        //生成用户密码的密钥，盐
        accountDO.setSecret("$1$" + CommonUtil.getStringNumRandom(8));
        //密码加密, 密码+盐处理
        String cryptPwd = Md5Crypt.md5Crypt(request.getPwd().getBytes(), accountDO.getSecret());
        accountDO.setPwd(cryptPwd);

        Integer rows = accountManager.insert(accountDO);
        log.info("rows:{},注册成功:{}", rows, accountDO.toString());

        //新用户注册成功，初始化信息，发放优惠券
        accountRegisterInitTask(accountDO);

        return JsonData.buildSuccess(accountDO);
    }


    /**
     * 新用户注册，初始化福利信息
     *
     * @param accountDO
     */
    private void accountRegisterInitTask(AccountDO accountDO) {
    }
}
