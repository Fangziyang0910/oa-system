package com.whaler.oasys.controller;

import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.whaler.oasys.model.enums.ResultCode;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.vo.ResultVo;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    /**
     * 处理自定义的ApiException异常。
     * @param e 抛出的ApiException异常实例
     * @return 返回一个包含异常信息的结果对象ResultVo
     */
    @ExceptionHandler(ApiException.class)
    public ResultVo<String> apiExceptionHandler(ApiException e){
        return new ResultVo<>(e.getResultCode(), e.getMsg());
    }

    /**
     * 处理方法参数不合法异常MethodArgumentNotValidException。
     * @param e 抛出的MethodArgumentNotValidException异常实例
     * @return 返回一个包含验证错误信息的结果对象ResultVo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        ObjectError objectError=e.getBindingResult().getAllErrors().get(0);
        return new ResultVo<>(ResultCode.VALIDATE_FAILED, objectError.getDefaultMessage());
    }
}
