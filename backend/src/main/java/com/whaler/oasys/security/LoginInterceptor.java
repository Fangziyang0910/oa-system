package com.whaler.oasys.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.whaler.oasys.model.enums.ResultCode;
import com.whaler.oasys.model.exception.ApiException;

import io.jsonwebtoken.Claims;

/**
 * 登录拦截器，用于拦截请求并进行登录验证
 * 继承自HandlerInterceptorAdapter，以实现自定义的拦截器逻辑
 */
public class LoginInterceptor
extends HandlerInterceptorAdapter {
    // 自动注入JwtManager，用于JWT token的解析和管理
    @Autowired
    private JwtManager jwtManager;

    /**
     * 在请求处理之前执行的预处理方法
     * 主要用于验证请求头中的Authorization信息，确保请求已授权
     * 
     * @param request  HttpServletRequest对象，代表客户端的HTTP请求
     * @param response HttpServletResponse对象，代表服务器的HTTP响应
     * @param handler  将要执行的处理器对象
     * @return boolean 返回false表示拦截请求，true表示放行请求
     * @throws Exception 抛出异常，用于处理预处理过程中的错误
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
     throws Exception {
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
        // 根据token中的subject信息，设置当前用户的ID
        UserContext.setCurrentUserId(
            Long.parseLong(
                claims.getSubject()
            )
        );
        return true; // 表示请求通过验证，可以继续处理
    }

    /**
     * 请求处理完成后执行的后处理方法
     * 主要用于清理用户上下文信息，确保线程安全
     * 
     * @param request  HttpServletRequest对象，代表客户端的HTTP请求
     * @param response HttpServletResponse对象，代表服务器的HTTP响应
     * @param handler  实际处理请求的处理器对象
     * @param ex       在处理过程中抛出的异常，可能为null
     * @throws Exception 抛出异常，用于处理后处理过程中的错误
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
     throws Exception {
        // 清理用户上下文信息
        UserContext.clear();
        super.afterCompletion(request, response, handler, ex); // 调用父类的后处理方法
    }
}
