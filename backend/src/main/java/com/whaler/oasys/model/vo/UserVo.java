package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserVo {
  private String name;
  private String token;
  private String email;
  private String phone;  
  private String city;
  private Long permissionId;
  private String department;
  private String role;
  private Boolean isApplicant;
  private Boolean isApprover;
  private Boolean isOperator;
}
