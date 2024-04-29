package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ProcessInstanceVo 类表示一个流程实例的视图对象。
 * 它用于存储流程实例的相关信息，包括流程实例的唯一标识和对应的流程定义信息。
 * @param processInstanceId 流程实例的唯一标识符
 * @param processDefinition 对应的流程定义信息
 */
@Data
@Accessors(chain = true)
public class ProcessInstanceVo {
    private String processInstanceId; // 流程实例的唯一标识符
    private ProcessDefinitionVo processDefinition; // 对应的流程定义信息
}
