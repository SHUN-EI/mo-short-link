package com.mo.service.impl;

import com.mo.interceptor.LoginInterceptor;
import com.mo.manager.DomainManager;
import com.mo.mapper.DomainMapper;
import com.mo.model.DomainDO;
import com.mo.model.LoginUserDTO;
import com.mo.service.DomainService;
import com.mo.vo.DomainVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2022/2/22
 */
@Service
public class DomainServiceImpl implements DomainService {

    @Autowired
    private DomainManager domainManager;

    /**
     * 查看账号下所有的域名
     *
     * @return
     */
    @Override
    public List<DomainVO> listAll() {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        List<DomainDO> officialDomainList = domainManager.listOfficialDomain();
        List<DomainDO> customDomainList = domainManager.listCustomDomain(loginUserDTO.getAccountNo());

        customDomainList.addAll(officialDomainList);

        List<DomainVO> resultList = customDomainList.stream().map(obj -> {
            DomainVO domainVO = new DomainVO();
            BeanUtils.copyProperties(obj, domainVO);
            return domainVO;
        }).collect(Collectors.toList());

        return resultList;
    }
}
