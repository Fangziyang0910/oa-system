package com.whaler.oasys.controller.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.enums.ResultCode;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.param.UserParam;
import com.whaler.oasys.model.vo.UserVo;
import com.whaler.oasys.security.JwtManager;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.UserService;
import com.whaler.oasys.tool.MyMesgSender;

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
    @Autowired
    private MyMesgSender myMesgSender;

    /**
     * 用户登录接口。
     * <p>
     * 通过接收前端发送的登录参数，验证用户登录信息，并返回用户相关信息。
     *
     * @param loginParam 包含用户登录信息的参数对象，必须是有效的。
     * @return UserVo 用户信息的视图对象，包含登录成功后用户的基本信息。
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public UserVo login(@RequestBody @Validated LoginParam loginParam){
        // 调用userService的login方法处理登录逻辑
        return userService.login(loginParam);
    }

    /**
     * 用户注册接口。
     * 通过接收前端发送的用户注册信息，调用userService的注册方法完成用户注册。
     *
     * @param userParam 包含用户注册信息的参数对象，必须是合法有效的。
     *                  通过@RequestBody注解从请求体中获取，@Validated注解确保数据有效性和完整性。
     * @return 该方法没有返回值，注册成功后直接返回空响应。
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public void register(@RequestBody @Validated UserParam userParam){
        userService.register(userParam); // 调用UserService的注册方法
    }

    /**
     * 查询当前用户的信息。
     * <p>
     * 该接口不需要接收任何参数，通过用户上下文获取当前用户的ID，然后查询并返回该用户的相关信息。
     *
     * @return UserVo 用户信息对象，包含用户的详细信息。
     */
    @PostMapping("/getUserInfo")
    public UserVo getUserInfo(){
        // 从用户上下文中获取当前用户的ID
        Long userId=UserContext.getCurrentUserId();
        // 根据用户ID查询用户信息
        return userService.selectByUserId(userId);
    }

    /**
     * 验证用户令牌的API接口。
     * 该方法通过获取请求头中的Authorization信息，解析并校验token的有效性。
     * 如果token有效，则返回"验证通过"，否则抛出未授权异常。
     *
     * @param request HttpServletRequest对象，用于获取请求头信息。
     * @return 返回字符串"验证通过"，表示令牌验证成功。
     * @throws ApiException 如果token不存在或无效，抛出此异常。
     */
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

    /**
     * 测试消息队列的功能。该方法通过接收用户姓名、消息名称和消息内容，
     * 并将这些信息通过消息队列发送出去。
     * 
     * @param userName 用户名，用于标识消息的发送者。
     * @param msgName 消息名称，用于标识消息的类型或主题。
     * @param msgContent 消息内容，发送的实际信息。
     * @return 该方法没有返回值。
     */
    @ApiOperation("测试消息队列")
    @PostMapping("/testMQ")
    public void testMQ(
        @RequestParam(value = "userName") String userName,
        @RequestParam(value = "msgName") String msgName,
        @RequestParam(value = "msgContent") String msgContent
    ){
        // 发送消息到消息队列
        myMesgSender.sendMessage(userName,msgName,msgContent);
    }
}
