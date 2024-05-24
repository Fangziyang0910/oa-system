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

@Service
public class PermissionServiceImpl
extends ServiceImpl<PermissionMapper, PermissionEntity>
implements PermissionService {
    /**
     * 插入权限实体
     * 
     * @param permissionParam 包含部门、角色等权限信息的参数对象
     * @throws ApiException 如果尝试插入的权限实体已存在，则抛出异常
     */
    @Override
    public void insertPermissionEntity(PermissionParam permissionParam) {
        // 查询条件：根据部门和角色查找已存在的权限实体
        LambdaQueryWrapper<PermissionEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PermissionEntity::getDepartment, permissionParam.getDepartment())
            .eq(PermissionEntity::getRole, permissionParam.getRole());
        PermissionEntity permissionEntity=baseMapper.selectOne(lambdaQueryWrapper);
        
        // 如果找到已存在的权限实体，则抛出异常
        if(permissionEntity!=null){
            throw new ApiException("该部门该角色已存在");
        }
        
        // 插入新的权限实体
        baseMapper.insertPermissionEntity(
            permissionParam.getDepartment(),
            permissionParam.getRole(),
            permissionParam.getIsApplicant(),
            permissionParam.getIsApprover(),
            permissionParam.getIsOperator()
        );
    }

    /**
     * 获取部门及其对应的角色信息。
     * <p>此方法不接受任何参数，返回一个表示部门和角色关系的JSON字符串。</p>
     * 
     * @return 返回一个字符串，这个字符串是通过将部门和它们对应的角色信息转换成JSON格式得到的。
     */
    @Override
    public String getDepartmentRoles() {
        // 创建一个查询包装器
        LambdaQueryWrapper<PermissionEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        // 根据查询包装器查询所有权限实体
        List<PermissionEntity>permissionEntities=this.baseMapper.selectList(lambdaQueryWrapper);
        // 查询所有部门
        List<String>departments=this.baseMapper.selectAllDepartments();
        // 创建一个映射，用于存储部门和它们对应的角色列表
        Map<String, List<String>>departmentRoles=new HashMap<>();
        for (String department : departments) {
            // 初始化部门的角色列表
            departmentRoles.put(department, new ArrayList<>());
            // 遍历所有权限实体，为每个部门添加相应的角色
            for (PermissionEntity permissionEntity : permissionEntities) {
                if (permissionEntity.getDepartment().equals(department)) {
                    // 如果权限实体的部门与当前部门匹配，则将角色添加到该部门的角色列表中
                    departmentRoles.get(department).add(permissionEntity.getRole());
                }
            }
        }
        // 将部门和角色的映射转换成JSON字符串
        String str=JSONObject.toJSONString(departmentRoles);
        return str;
    }

    /**
     * 获取所有部门的信息。
     * 
     * <p>此方法通过调用{@code baseMapper}的{@code selectAllDepartments}方法，
     * 查询所有部门的信息，并将查询结果转化为JSON格式的字符串返回。</p>
     *
     * @return 返回包含所有部门信息的JSON字符串。
     */
    @Override
    public String getDepartments() {
        // 从数据库查询所有部门信息
        List<String>departments=this.baseMapper.selectAllDepartments();
        // 将查询结果转换为JSON字符串
        return JSONObject.toJSONString(departments);
    }
}
