package com.whaler.oasys.model.param;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录参数封装类
 * 用于封装用户登录时需要提供的用户名和密码信息
 * @param name 用户名
 * @param password 密码
 */
@Data
@Valid
@Accessors(chain = true)
public class LoginParam {
    // 用户名不能为空，长度在1-20个字符之间
    @NotNull(message = "用户名不能为空")
    @Length(min = 1, max = 20, message = "用户名长度在1-20之间")
    private String name;
    
    // 密码不能为空，长度在1-30个字符之间
    @NotNull(message = "密码不能为空")
    @Length(min = 1, max = 30, message = "密码长度在1-30之间")
    private String password;
}
