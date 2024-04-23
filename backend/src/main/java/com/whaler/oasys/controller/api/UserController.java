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

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public UserVo login(@RequestBody @Validated LoginParam loginParam){
        return userService.login(loginParam);
    }

    @PostMapping("/register")
    public void register(@RequestBody @Validated UserParam userParam){
        userService.register(userParam);
    }
}
