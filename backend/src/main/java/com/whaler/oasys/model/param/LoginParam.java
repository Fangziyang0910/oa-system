package com.whaler.oasys.model.param;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Valid
@Accessors(chain = true)
public class LoginParam {
    @NotNull(message = "用户名不能为空")
    @Length(min = 1, max = 20, message = "用户名长度在1-20之间")
    private String name;
    @NotNull(message = "密码不能为空")
    @Length(min = 1, max = 30, message = "密码长度在1-30之间")
    private String password;
}
