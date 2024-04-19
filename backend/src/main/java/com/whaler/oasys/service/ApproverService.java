package com.whaler.oasys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.ApproverEntity;
import com.whaler.oasys.model.vo.ApproverVo;

public interface ApproverService
extends IService<ApproverEntity> {
    int insertApproverEntity(Long approverId, String processinstanceId);

    int deleteApproverEntity(Long approverId, String processinstanceId);

    ApproverVo selectByApproverId(Long approverId);
}