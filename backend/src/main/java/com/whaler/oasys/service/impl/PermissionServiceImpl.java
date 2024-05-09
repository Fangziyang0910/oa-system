package com.whaler.oasys.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.PermissionMapper;
import com.whaler.oasys.model.entity.PermissionEntity;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.PermissionParam;
import com.whaler.oasys.service.PermissionService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

    @Override
    public String getDepartmentRoles() {
        LambdaQueryWrapper<PermissionEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<PermissionEntity>permissionEntities=this.baseMapper.selectList(lambdaQueryWrapper);
        List<String>departments=this.baseMapper.selectAllDepartments();
        Map<String, List<String>>departmentRoles=new HashMap<>();
        for (String department : departments) {
            departmentRoles.put(department, new ArrayList<>());
            for (PermissionEntity permissionEntity : permissionEntities) {
                if (permissionEntity.getDepartment().equals(department)) {
                    departmentRoles.get(department).add(permissionEntity.getRole());
                }
            }
        }
        String str=JSONObject.toJSONString(departmentRoles);
        return str;
    }
}
