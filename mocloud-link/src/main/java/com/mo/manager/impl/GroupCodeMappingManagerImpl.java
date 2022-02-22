package com.mo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.manager.GroupCodeMappingManager;
import com.mo.mapper.GroupCodeMappingMapper;
import com.mo.model.GroupCodeMappingDO;
import com.mo.request.GroupCodeMappingUpdateRequest;
import com.mo.request.ShortLinkPageRequest;
import com.mo.vo.GroupCodeMappingVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by mo on 2022/2/22
 */
@Component
public class GroupCodeMappingManagerImpl implements GroupCodeMappingManager {

    @Autowired
    private GroupCodeMappingMapper mappingMapper;

    @Override
    public GroupCodeMappingDO findByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId) {

        GroupCodeMappingDO codeMappingDO = mappingMapper.selectOne(new QueryWrapper<GroupCodeMappingDO>().eq("id", mappingId)
                .eq("account_no", accountNo)
                .eq("group_id", groupId));

        return codeMappingDO;
    }

    @Override
    public Integer add(GroupCodeMappingDO groupCodeMappingDO) {
        return mappingMapper.insert(groupCodeMappingDO);
    }

    /**
     * 逻辑删除
     *
     * @param shortLinkCode
     * @param accountNo
     * @param groupId
     * @return
     */
    @Override
    public Integer delete(String shortLinkCode, Long accountNo, Long groupId) {

        Integer rows = mappingMapper.update(null, new UpdateWrapper<GroupCodeMappingDO>().eq("code", shortLinkCode)
                .eq("account_no", accountNo)
                .eq("group_id", groupId)
                .set("del", 1));

        return rows;
    }

    @Override
    public Map<String, Object> pageShortLink(ShortLinkPageRequest request) {

        Page<GroupCodeMappingDO> pageInfo = new Page<>(request.getPage(), request.getSize());

        Page<GroupCodeMappingDO> mappingDOPage = mappingMapper.selectPage(pageInfo, new QueryWrapper<GroupCodeMappingDO>()
                .eq("account_no", request.getAccountNo())
                .eq("group_id", request.getGroupId()));

        List<GroupCodeMappingDO> groupCodeMappingDOS = mappingDOPage.getRecords();
        List<GroupCodeMappingVO> groupCodeMappingVOS = groupCodeMappingDOS.stream().map(obj -> beanProcess(obj)).collect(Collectors.toList());

        Map<String, Object> resultMap = new HashMap<>(3);
        resultMap.put("total_record", mappingDOPage.getTotal());
        resultMap.put("total_page", mappingDOPage.getPages());
        resultMap.put("current_data", groupCodeMappingVOS);

        return resultMap;
    }

    @Override
    public Integer updateGroupCodeMappingState(GroupCodeMappingUpdateRequest request) {

        Integer rows = mappingMapper.update(null, new UpdateWrapper<GroupCodeMappingDO>()
                .eq("code", request.getShortLinkCode())
                .eq("account_no", request.getAccountNo())
                .eq("group_id", request.getGroupId())
                .set("state", request.getShortLinkStateEnum().name()));

        return rows;
    }

    private GroupCodeMappingVO beanProcess(GroupCodeMappingDO groupCodeMappingDO) {
        GroupCodeMappingVO groupCodeMappingVO = new GroupCodeMappingVO();
        BeanUtils.copyProperties(groupCodeMappingDO, groupCodeMappingVO);

        return groupCodeMappingVO;
    }
}
