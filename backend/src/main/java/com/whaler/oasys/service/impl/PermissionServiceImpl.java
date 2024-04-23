package com.whaler.oasys.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.PermissionMapper;
import com.whaler.oasys.model.entity.PermissionEntity;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.PermissionParam;
import com.whaler.oasys.service.PermissionService;

@Service
public class PermissionServiceImpl
extends ServiceImpl<PermissionMapper, PermissionEntity>
implements PermissionService {
    @Override
    public void insertPermissionEntity(PermissionParam permissionParam) {
        LambdaQueryWrapper<PermissionEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PermissionEntity::getDepartment, permissionParam.getDepartment())
            .eq(PermissionEntity::getRole, permissionParam.getRole());
        PermissionEntity permissionEntity=baseMapper.selectOne(lambdaQueryWrapper);
        if(permissionEntity!=null){
            throw new ApiException("该部门该角色已存在");
        }
        baseMapper.insertPermissionEntity(
            permissionParam.getDepartment(),
            permissionParam.getRole(),
            permissionParam.getIsApplicant(),
            permissionParam.getIsApprover(),
            permissionParam.getIsOperator()
        );
    }
}
