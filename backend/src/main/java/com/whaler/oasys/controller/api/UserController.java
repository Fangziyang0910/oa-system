package com.whaler.oasys.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.param.UserParam;
import com.whaler.oasys.model.vo.UserVo;
import com.whaler.oasys.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(description = "用户")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public UserVo login(@RequestBody @Validated LoginParam loginParam){
        return userService.login(loginParam);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public void register(@RequestBody @Validated UserParam userParam){
        userService.register(userParam);
    }
}
