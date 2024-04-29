package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ProcessDefinitionVo 类用于表示一个流程定义的视图对象。
 * 它包含了流程定义的各种基本信息。
 * @param processDefinitionId 流程定义的唯一标识符
 * @param processDefinitionKey 流程定义的键
 * @param processDefinitionName 流程定义的名称
 * @param processDefinitionCategory 流程定义的类别
 * @param processDefinitionDescription 流程定义的描述
 * @param processDefinitionVersion 流程定义的版本号
 */
@Data
@Accessors(chain = true)
public class ProcessDefinitionVo {
    private String processDefinitionId; // 流程定义的唯一标识符
    private String processDefinitionKey; // 流程定义的键
    private String processDefinitionName; // 流程定义的名称
    private String processDefinitionCategory; // 流程定义的类别
    private String processDefinitionDescription; // 流程定义的描述
    private Integer processDefinitionVersion; // 流程定义的版本号
}
