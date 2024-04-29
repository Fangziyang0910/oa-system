package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * UserVo 类定义了用户视图对象，用于表示用户的相关信息。
 * 该类包含了用户的基本信息、权限、部门、角色以及申请、审批和操作权限的状态。
 * @param userId 用户ID
 * @param name 用户名
 * @param token 用户令牌
 * @param email 电子邮箱地址
 * @param phone 电话号码
 * @param city 所在城市
 * @param permissionId 权限ID
 * @param department 所属部门
 * @param role 角色标识
 * @param isApplicant 是否为申请人
 * @param isApprover 是否为审批人
 * @param isOperator 是否为操作员
 */
@Data
@Accessors(chain = true)
public class UserVo {
  private Long userId; // 用户ID
  private String name; // 用户名
  private String token; // 用户令牌
  private String email; // 电子邮箱地址
  private String phone; // 电话号码
  private String city; // 所在城市
  private Long permissionId; // 权限ID
  private String department; // 所属部门
  private String role; // 角色标识
  private Boolean isApplicant; // 是否为申请人
  private Boolean isApprover; // 是否为审批人
  private Boolean isOperator; // 是否为操作员
}
