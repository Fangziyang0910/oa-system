package com.whaler.oasys.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 权限实体类，用于表示系统中的权限信息。
 * 继承自基础实体类（BaseEntity），并添加了权限相关的属性。
 * 在数据库中对应的表名为"permission"。
 * 
 * @param department 部门，表示权限所属的部门
 * @param role 角色，表示权限关联的角色
 * @param isApplicant 申请人标志，标识是否拥有申请权限的资格
 * @param isApprover 审批人标志，标识是否拥有审批权限的资格
 * @param isOperator 操作人标志，标识是否拥有操作权限的资格
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("permission")
public class PermissionEntity
extends BaseEntity {
    private String department; // 部门，表示权限所属的部门
    private String role; // 角色，表示权限关联的角色
    private Boolean isApplicant; // 申请人标志，标识是否拥有申请权限的资格
    private Boolean isApprover; // 审批人标志，标识是否拥有审批权限的资格
    private Boolean isOperator; // 操作人标志，标识是否拥有操作权限的资格
}
