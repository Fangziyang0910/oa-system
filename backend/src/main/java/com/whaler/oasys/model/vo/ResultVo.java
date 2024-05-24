package com.whaler.oasys.model.vo;

import com.whaler.oasys.model.enums.ResultCode;

import lombok.Data;

@Data
/**
 * ResultVo类用于封装响应结果
 * @param <T> 泛型，表示响应的具体数据类型
 * @param code 状态码，用于表示操作的执行结果
 * @param msg 响应信息，用于描述操作结果的详细信息
 * @param data 响应数据，泛型，具体类型由调用方指定
 */
public class ResultVo<T> {
    // 状态码，用于表示操作的执行结果
    private int code;
    // 响应信息，用于描述操作结果的详细信息
    private String msg;
    // 响应的具体数值，泛型，具体类型由调用方指定
    private T data;

    /**
     * 构造函数，用于创建一个包含数据的ResultVo实例
     * @param data 响应数据
     */
    public ResultVo(T data){
        this(ResultCode.SUCCESS, data); // 调用另一个构造函数完成初始化
    }

    /**
     * 构造函数，用于创建一个带有结果码和数据的ResultVo实例
     * @param resultCode 结果码，定义了操作的结果状态和信息
     * @param data 响应数据
     */
    public ResultVo(ResultCode resultCode, T data){
        this.code=resultCode.getCode(); // 初始化状态码
        this.msg=resultCode.getMsg(); // 初始化响应信息
        this.data=data; // 初始化响应数据
    }
}
