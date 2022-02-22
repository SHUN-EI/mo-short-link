package com.mo.manager;

import com.mo.enums.DomainTypeEnum;
import com.mo.model.DomainDO;

import java.util.List;

/**
 * Created by mo on 2022/2/22
 */
public interface DomainManager {

    DomainDO findById(Long id, Long accountNO);

    DomainDO findByDomainTypeAndID(Long id, DomainTypeEnum domainTypeEnum);

    Integer add(DomainDO domainDO);

    List<DomainDO> listOfficialDomain();

    List<DomainDO> listCustomDomain(Long accountNo);
}
