package com.whaler.oasys.model.param;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Valid
@Accessors(chain = true)
public class PermissionParam {
    @NotNull(message = "部门不能为空")
    @Length(min = 1, max = 20, message = "部门长度在1-20之间")
    private String department;
    @NotNull(message = "角色不能为空")
    @Length(min = 1, max = 20, message = "角色长度在1-20之间")
    private String role;
    @NotNull(message = "申请人权限不能为空")
    private Boolean isApplicant;
    @NotNull(message = "审批人权限不能为空")
    private Boolean isApprover;
    @NotNull(message = "操作员权限不能为空")
    private Boolean isOperator;
}
