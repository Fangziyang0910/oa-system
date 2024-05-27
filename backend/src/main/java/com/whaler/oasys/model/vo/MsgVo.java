package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * MsgVo类用于定义消息实体的封装。
 * 通过@Data和@Accessors(chain = true)注解，提供了链式访问器和自动生成功能。
 * @param msgId 消息ID
 * @param sendTime 消息发送时间
 * @param msgName 消息名称或标题
 * @param msgContent 消息的内容或正文
 */
@Accessors(chain = true)
@Data
public class MsgVo {
    // 消息ID，用于唯一标识一条消息
    private String msgId;
    // 消息发送时间，格式为字符串
    private String sendTime;
    // 消息名称或标题
    private String msgName;
    // 消息的内容或正文
    private String msgContent;
}
