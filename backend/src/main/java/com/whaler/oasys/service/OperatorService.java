package com.whaler.oasys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.OperatorEntity;
import com.whaler.oasys.model.vo.OperatorVo;

public interface OperatorService
extends IService<OperatorEntity> {
    int insertOperatorEntity(Long operatorId, String processinstanceId);
    
    int deleteOperatorEntity(Long operatorId, String processinstanceId);

    OperatorVo selectByOperatorId(Long operatorId);
}
