package com.mo.manager;

import com.mo.model.GroupCodeMappingDO;
import com.mo.request.GroupCodeMappingUpdateRequest;
import com.mo.request.ShortLinkPageRequest;

import java.util.Map;

/**
 * Created by mo on 2022/2/22
 */
public interface GroupCodeMappingManager {

    GroupCodeMappingDO findByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId);

    Integer add(GroupCodeMappingDO groupCodeMappingDO);

    Integer delete(GroupCodeMappingDO groupCodeMappingDO);

    Map<String, Object> pageShortLink(ShortLinkPageRequest request);

    Integer updateGroupCodeMappingState(GroupCodeMappingUpdateRequest request);

    GroupCodeMappingDO findByCodeAndGroupId(String shortLinkCode, Long id, Long accountNo);

    Integer update(GroupCodeMappingDO groupCodeMappingDO);
}
