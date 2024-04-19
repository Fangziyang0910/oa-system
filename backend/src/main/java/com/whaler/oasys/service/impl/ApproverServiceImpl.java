package com.whaler.oasys.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.ApproverMapper;
import com.whaler.oasys.model.entity.ApproverEntity;
import com.whaler.oasys.model.vo.ApproverVo;
import com.whaler.oasys.service.ApproverService;

@Service
public class ApproverServiceImpl
extends ServiceImpl<ApproverMapper,ApproverEntity>
implements ApproverService {
    @Override
    public int insertApproverEntity(Long approverId, String processinstanceId) {
        return this.baseMapper.insertApproverEntity(approverId, processinstanceId);
    }

    @Override
    public int deleteApproverEntity(Long approverId, String processinstanceId) {
        return this.baseMapper.deleteApproverEntity(approverId, processinstanceId);
    }

    @Override
    public ApproverVo selectByApproverId(Long approverId) {
        List<ApproverEntity> approverEntities = this.baseMapper.selectByApproverId(approverId);
        ApproverVo approverVo = new ApproverVo();
        approverVo.setApproverId(approverId);
        approverVo.setProcessinstanceIds(
            approverEntities.stream()
            .map(ApproverEntity::getProcessinstanceId)
            .collect(Collectors.toSet())
        );
        return approverVo;
    }
}
