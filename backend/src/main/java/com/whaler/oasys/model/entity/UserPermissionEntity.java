package com.whaler.oasys.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户权限实体类
 * 用于定义用户在特定部门和角色下的权限信息
 * 
 * @param name 用户名
 * @param department 所属部门
 * @param role 用户角色
 * @param isApplicant 是否为申请人
 * @param isApprover 是否为审批人
 * @param isOperator 是否为操作人
 */
@Data
@Accessors(chain = true)
public class UserPermissionEntity
extends BaseEntity {
    private String name; // 用户名
    private String department; // 所属部门
    private String role; // 用户角色
    private Boolean isApplicant; // 是否为申请人
    private Boolean isApprover; // 是否为审批人
    private Boolean isOperator; // 是否为操作人
}