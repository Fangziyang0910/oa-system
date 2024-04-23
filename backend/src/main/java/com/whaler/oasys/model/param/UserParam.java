package com.whaler.oasys.model.param;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Valid
@Accessors(chain = true)
public class UserParam {
    @NotNull(message = "用户名不能为空")
    @Length(min = 1, max = 20, message = "用户名长度在1-20之间")
    private String name;
    @NotNull(message = "密码不能为空")
    @Length(min = 1, max = 30, message = "密码长度在1-30之间")
    private String password;
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    @NotNull(message = "手机号不能为空")
    private String phone;
    @NotNull(message = "城市不能为空")
    private String city;
    @NotNull(message = "权限id不能为空")
    private Long permissionId;
}
