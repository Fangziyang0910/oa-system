package com.whaler.oasys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.PermissionEntity;
import com.whaler.oasys.model.param.PermissionParam;

/**
 * 权限服务接口，提供权限相关操作的方法。
 * 继承自IService<PermissionEntity>，针对PermissionEntity实体提供服务。
 */
public interface PermissionService
extends IService<PermissionEntity> {
    
    /**
     * 插入权限实体。
     * 
     * @param permissionParam 包含权限信息的参数对象，用于插入新的权限实体。
     */
    void insertPermissionEntity(PermissionParam permissionParam);

    /**
     * 获取部门角色信息。
     * 
     * @return 返回部门角色的相关信息，以字符串形式返回。
     */
    String getDepartmentRoles();

    /**
     * 获取所有部门信息。
     * 
     * @return 返回所有部门的信息，以字符串形式返回。
     */
    String getDepartments();
} 