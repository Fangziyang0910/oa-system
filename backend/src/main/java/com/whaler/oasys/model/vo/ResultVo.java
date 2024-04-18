package com.whaler.oasys.model.vo;

import com.whaler.oasys.model.enums.ResultCode;

import lombok.Data;

@Data
public class ResultVo<T> {
    // 状态码
    private int code;
    // 响应信息
    private String msg;
    // 响应的具体数值
    private T data;

    public ResultVo(T data){
        this(ResultCode.SUCCESS, data);
    }

    public ResultVo(ResultCode resultCode, T data){
        this.code=resultCode.getCode();
        this.msg=resultCode.getMsg();
        this.data=data;
    }
}
