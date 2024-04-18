package com.whaler.oasys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whaler.oasys.model.entity.PermissionEntity;

@Repository
@Mapper
public interface PermissionMapper
extends BaseMapper<PermissionEntity> {
    /**
     * 插入权限实体
     * 
     * @param department 部门，指定权限所属的部门
     * @param role 角色，指定权限关联的角色
     * @param isApprover 是否为审批人，标识该权限是否包含审批权限
     * @param isApplicant 是否为申请人，标识该权限是否允许申请
     * @param isOperator 是否为操作人，标识该权限是否为操作权限
     * @return 返回插入操作影响的行数，通常为1表示插入成功
     */
    int insertPermissionEntity(
        @Param("department") String department,
        @Param("role") String role,
        @Param("isApplicant") Boolean isApplicant,
        @Param("isApprover") Boolean isApprover,
        @Param("isOperator") Boolean isOperator
    );

    int deleteByDepartmentRole(
        @Param("department")String department, 
        @Param("role")String role
    );

    /**
     * 根据部门查询权限实体列表。
     * 
     * @param department 部门名称，用于筛选权限实体的条件。
     * @return 返回一个权限实体列表，这些实体属于指定的部门。
     */
    List<PermissionEntity> selectByDepartment(String department);

    /**
     * 根据角色查询权限实体列表。
     * 
     * @param role 角色的名称，用于查询与此角色关联的权限实体。
     * @return 返回一个权限实体列表，这些实体与给定的角色相关联。
     */
    List<PermissionEntity> selectByRole(String role);

    /**
     * 根据是否为申请人查询权限实体列表。
     * 
     * @param isApprover 是否为审批人。true表示是审批人，false表示不是审批人。
     * @return 返回权限实体列表。列表中包含的是与申请人身份相关的权限实体。
     */
    List<PermissionEntity> selectByIsApplicant(Boolean isApprover);

    /**
     * 根据是否为审批人查询权限实体列表。
     * 
     * @param isApprover Boolean类型，表示是否为审批人。如果为true，表示查询审批人相关的权限实体；如果为false，表示查询非审批人相关的权限实体。
     * @return 返回一个PermissionEntity类型的列表，包含了根据isApprover条件查询到的权限实体。
     */
    List<PermissionEntity> selectByIsApprover(Boolean isApprover);

    /**
     * 根据是否为操作员查询权限实体列表。
     * 
     * @param isOperator 指示是否为操作员的布尔值。如果为true，表示查询操作员的权限；如果为false，表示查询非操作员的权限。
     * @return 返回一个权限实体列表，这些实体根据指定的是否为操作员条件进行筛选。
     */
    List<PermissionEntity> selectByIsOperator(Boolean isOperator);
}
