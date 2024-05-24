package com.whaler.oasys.model.param;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 管理员参数类，用于验证和存储管理员的注册信息。
 * 通过注解限制了字段的合法性，确保了输入数据的正确性。
 * @param name 用户名
 * @param password 密码
 * @param email 邮箱
 * @param phone 手机号
 */
@Data
@Valid
@Accessors(chain = true)
public class AdministratorParam {
    // 用户名字段，不能为空，长度在1-20字符之间
    @NotNull(message = "用户名不能为空")
    @Length(min = 1, max = 20, message = "用户名长度在1-20之间")
    private String name;
    
    // 密码字段，不能为空，长度在1-30字符之间
    @NotNull(message = "密码不能为空")
    @Length(min = 1, max = 30, message = "密码长度在1-30之间")
    private String password;
    
    // 邮箱字段，不能为空，且必须符合邮箱格式
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    // 手机号字段，不能为空
    @NotNull(message = "手机号不能为空")
    private String phone;
    
}
