package com.whaler.oasys.model.enums;

import lombok.Getter;

@Getter
/**
 * 结果码枚举类，用于定义操作结果的通用状态码及其对应信息。
 */
public enum ResultCode {

    // 成功操作的结果码
    SUCCESS(0000,"操作成功"),
    // 未授权访问的结果码
    UNAUTHORIZED(1001,"没有登录"),
    // 权限不足的结果码
    FORBIDDEN(1002,"没有相关权限"),
    // 参数校验失败的结果码
    VALIDATE_FAILED(1003,"参数校验失败"),
    // 接口调用失败的结果码
    FAILED(1004,"接口异常"),
    // 未知错误的结果码
    ERROR(5000,"未知错误");

    private int code; // 结果码的数字部分
    private String msg; // 结果码对应的信息文本
    
    /**
     *  枚举构造函数，用于初始化结果码及其对应的信息。
     * @param code 结果码的数字部分。
     * @param msg 结果码对应的信息文本。
     */
    ResultCode(int code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
