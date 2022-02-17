package com.mo.service.impl;

import com.mo.enums.BizCodeEnum;
import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.LinkGroupManager;
import com.mo.model.LinkGroupDO;
import com.mo.model.LoginUserDTO;
import com.mo.request.LinkGroupAddRequest;
import com.mo.request.LinkGroupDetailRequest;
import com.mo.service.LinkGroupService;
import com.mo.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by mo on 2022/2/17
 */
@Service
public class LinkGroupServiceImpl implements LinkGroupService {

    @Autowired
    private LinkGroupManager linkGroupManager;

    /**
     * 删除短链分组
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData delete(LinkGroupDetailRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        //accountNo，账号唯一标识，防止越权删除
        int rows = linkGroupManager.del(request.getId(), loginUserDTO.getAccountNo());

        return rows == 1 ? JsonData.buildSuccess() : JsonData.buildResult(BizCodeEnum.GROUP_NOT_EXIST);
    }

    /**
     * 添加短链分组
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData add(LinkGroupAddRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        LinkGroupDO linkGroupDO = new LinkGroupDO();
        linkGroupDO.setTitle(request.getTitle());
        linkGroupDO.setAccountNo(loginUserDTO.getAccountNo());

        int rows = linkGroupManager.add(linkGroupDO);

        return rows == 1 ? JsonData.buildSuccess() : JsonData.buildResult(BizCodeEnum.GROUP_ADD_FAIL);
    }
}
