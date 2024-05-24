package com.whaler.oasys.model.param;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 权限参数类，用于封装权限相关的请求参数。
 * 主要包括部门、角色、申请人权限、审批人权限、操作员权限相关信息。
 * @param department 部门名称
 * @param role 角色名称
 * @param isApplicant 申请人是否有权限
 * @param isApprover 审批人是否有权限
 * @param isOperator 操作员是否有权限
 */
@Data
@Valid
@Accessors(chain = true)
public class PermissionParam {
    // 部门名称，不能为空，长度限制在1-20字符之间
    @NotNull(message = "部门不能为空")
    @Length(min = 1, max = 20, message = "部门长度在1-20之间")
    private String department;
    
    // 角色名称，不能为空，长度限制在1-20字符之间
    @NotNull(message = "角色不能为空")
    @Length(min = 1, max = 20, message = "角色长度在1-20之间")
    private String role;
    
    // 申请人是否有权限，不能为空
    @NotNull(message = "申请人权限不能为空")
    private Boolean isApplicant;
    
    // 审批人是否有权限，不能为空
    @NotNull(message = "审批人权限不能为空")
    private Boolean isApprover;
    
    // 操作员是否有权限，不能为空
    @NotNull(message = "操作员权限不能为空")
    private Boolean isOperator;
}
