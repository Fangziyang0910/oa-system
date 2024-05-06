package com.whaler.oasys.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.enums.ResultCode;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.param.UserParam;
import com.whaler.oasys.model.vo.UserVo;
import com.whaler.oasys.security.JwtManager;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.UserService;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(description = "用户")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtManager jwtManager;

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

    @ApiOperation("查询本用户信息")
    @PostMapping("/getUserInfo")
    public UserVo getUserInfo(){
        Long userId=UserContext.getCurrentUserId();
        return userService.selectByUserId(userId);
    }

    @ApiOperation("验证用户令牌")
    @GetMapping("/validateToken")
    public String validateToken(HttpServletRequest request){
        // 从请求头中获取Authorization信息
        String token=request.getHeader("Authorization");
        // 如果token不存在，则抛出未授权异常
        if(token==null){
            throw new ApiException(ResultCode.UNAUTHORIZED);
        }
        // 解析token，并校验其有效性
        Claims claims=jwtManager.parse(token);
        // 如果解析失败或token无效，则抛出未授权异常
        if(claims==null){
            throw new ApiException(ResultCode.UNAUTHORIZED);
        }
        return "验证通过"; // 表示请求通过验证，可以继续处理
    }
}
