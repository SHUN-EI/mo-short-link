package com.mo.service.impl;

import com.mo.enums.BizCodeEnum;
import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.LinkGroupManager;
import com.mo.model.LinkGroupDO;
import com.mo.model.LoginUserDTO;
import com.mo.request.LinkGroupAddRequest;
import com.mo.request.LinkGroupDetailRequest;
import com.mo.request.LinkGroupUpdateRequest;
import com.mo.service.LinkGroupService;
import com.mo.utils.JsonData;
import com.mo.vo.LinkGroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2022/2/17
 */
@Service
public class LinkGroupServiceImpl implements LinkGroupService {

    @Autowired
    private LinkGroupManager linkGroupManager;


    /**
     * 更新短链分组
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData update(LinkGroupUpdateRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        LinkGroupDO linkGroupDO = new LinkGroupDO();
        linkGroupDO.setTitle(request.getTitle());
        linkGroupDO.setId(request.getId());
        linkGroupDO.setAccountNo(loginUserDTO.getAccountNo());

        int rows = linkGroupManager.update(linkGroupDO);

        return rows == 1 ? JsonData.buildSuccess(linkGroupDO.toString()) : JsonData.buildResult(BizCodeEnum.GROUP_OPER_FAIL);
    }

    /**
     * 查看账号下所有的短链分组
     *
     * @return
     */
    @Override
    public List<LinkGroupVO> findAccountAllLinkGroup() {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        List<LinkGroupDO> linkGroupDOList = linkGroupManager.findAccountAllLinkGroup(loginUserDTO.getAccountNo());

        List<LinkGroupVO> linkGroupVOList = linkGroupDOList.stream().map(obj -> {
            LinkGroupVO linkGroupVO = new LinkGroupVO();
            BeanUtils.copyProperties(obj, linkGroupVO);
            return linkGroupVO;
        }).collect(Collectors.toList());

        return linkGroupVOList;
    }

    /**
     * 查看短链分组详情
     *
     * @param request
     * @return
     */
    @Override
    public JsonData detail(LinkGroupDetailRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        LinkGroupDO linkGroupDO = linkGroupManager.detail(request.getId(), loginUserDTO.getAccountNo());

        LinkGroupVO linkGroupVO = new LinkGroupVO();
        BeanUtils.copyProperties(linkGroupDO, linkGroupVO);

        return JsonData.buildSuccess(linkGroupVO);
    }

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

        return rows == 1 ? JsonData.buildSuccess(linkGroupDO.toString()) : JsonData.buildResult(BizCodeEnum.GROUP_ADD_FAIL);
    }
}
