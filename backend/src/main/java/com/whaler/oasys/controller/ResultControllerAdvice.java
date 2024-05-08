package com.whaler.oasys.controller;


import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.vo.ResultVo;

/**
 * 用于统一处理API响应结果的控制器建议类。
 * 通过实现ResponseBodyAdvice接口，此类能够对Controller方法的返回值进行拦截和处理，
 * 从而实现统一的响应体格式。
 */
@RestControllerAdvice(basePackages = {"com.whaler.oasys.controller.api"})
public class ResultControllerAdvice
implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否对特定返回类型的应用进行处理。
     * @param returnType 方法返回类型
     * @param aClass Http消息转换器的类
     * @return 如果方法返回类型不是ResultVo，则返回true，表示需要处理。
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
        return !returnType.getParameterType().equals(ResultVo.class);
    }

    /**
     * 在响应体写入之前对响应体进行处理，封装成统一的ResultVo格式。
     * @param body 原始响应体对象
     * @param returnType 方法返回类型
     * @param mediaType 媒体类型
     * @param aClass Http消息转换器的类
     * @param serverHttpRequest 服务器HttpRequest对象
     * @param serverHttpResponse 服务器HttpResponse对象
     * @return 封装后的ResultVo对象，如果原始类型为String，则返回String类型的ResultVo封装结果。
     */
    @Override
    public Object beforeBodyWrite(
        Object body, 
        MethodParameter returnType, 
        MediaType mediaType, 
        Class<? extends HttpMessageConverter<?>> aClass, 
        ServerHttpRequest serverHttpRequest, 
        ServerHttpResponse serverHttpResponse
    ) {
        // 当返回类型为String时，将body封装成ResultVo<String>格式
        if(returnType.getParameterType().equals(String.class)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(new ResultVo<>(body));
            }catch(JsonProcessingException e){
                // 处理JSON转换异常，抛出ApiException
                throw new ApiException("返回String类型错误");
            }
        }
        // 对于非String类型的返回值，直接封装成ResultVo<T>格式
        else if(returnType.getParameterType().equals(byte[].class)){
            return body;
        }
        return new ResultVo<>(body);
    }
}
