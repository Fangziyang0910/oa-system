package com.whaler.oasys.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.OperatorMapper;
import com.whaler.oasys.model.entity.OperatorEntity;
import com.whaler.oasys.model.vo.OperatorVo;
import com.whaler.oasys.service.OperatorService;

@Service
public class OperatorServiceImpl
extends ServiceImpl<OperatorMapper,OperatorEntity>
implements OperatorService {
    @Override
    public int insertOperatorEntity(Long operatorId, String processinstanceId) {
        return this.baseMapper.insertOperatorEntity(operatorId, processinstanceId);
    }

    @Override
    public int deleteOperatorEntity(Long operatorId, String processinstanceId) {
        return this.baseMapper.deleteOperatorEntity(operatorId, processinstanceId);
    }

    @Override
    public OperatorVo selectByOperatorId(Long operatorId) {
        List<OperatorEntity> operatorEntities = this.baseMapper.selectByOperatorId(operatorId);
        OperatorVo operatorVo=new OperatorVo();
        operatorVo.setOperatorId(operatorId);
        operatorVo.setProcessinstanceIds(
            operatorEntities.stream()
            .map(OperatorEntity::getProcessinstanceId)
            .collect(Collectors.toSet())
        );
        return operatorVo;
    }
}
