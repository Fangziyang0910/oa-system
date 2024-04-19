package com.whaler.oasys.model.exception;

import com.whaler.oasys.model.enums.ResultCode;

import lombok.Getter;

@Getter
public class ApiException
extends RuntimeException {
    private final String msg;
    private final ResultCode resultCode;

    public ApiException(ResultCode resultCode,String msg) {
        super(msg);
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public ApiException(ResultCode resultCode) {
        this(resultCode, resultCode.getMsg());
    }

    public ApiException(String msg) {
        this(ResultCode.FAILED,msg);
    }

    public ApiException(){
        this(ResultCode.FAILED);
    }
}
