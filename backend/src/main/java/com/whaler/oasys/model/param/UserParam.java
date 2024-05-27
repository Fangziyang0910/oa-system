package com.whaler.oasys.model.param;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户参数验证类，用于封装前端传入的用户相关参数，并进行合法性校验。
 * @param name 用户名
 * @param password 密码
 * @param email 邮箱  
 * @param phone 手机号
 * @param city 城市
 * @param permissionId 权限ID
 */
@Data
@Valid
@Accessors(chain = true)
public class UserParam {
    // 用户名，不能为空，长度在1-20字符之间
    @NotNull(message = "用户名不能为空")
    @Length(min = 1, max = 20, message = "用户名长度在1-20之间")
    private String name;
    
    // 密码，不能为空，长度在1-30字符之间
    @NotNull(message = "密码不能为空")
    @Length(min = 1, max = 30, message = "密码长度在1-30之间")
    private String password;
    
    // 邮箱，不能为空，且必须符合邮箱格式
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    // 手机号，不能为空
    @NotNull(message = "手机号不能为空")
    private String phone;
    
    // 城市，不能为空
    @NotNull(message = "城市不能为空")
    private String city;
    
    // 权限ID，不能为空
    @NotNull(message = "权限id不能为空")
    private Long permissionId;
}
