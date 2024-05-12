package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务视图对象类，用于封装任务的相关信息。
 * @param taskId 任务ID
 * @param taskName 任务名称
 * @param executionId 执行ID
 * @param starterName 任务发起人
 * @param assigneeName 任务执行人
 * @param processDefinitionKey 流程定义标识
 * @param description 任务描述
 * @param dueTime 任务到期时间
 * @param endTime 任务结束时间
 */
@Data
@Accessors(chain = true)
public class TaskVo {
    private String taskId; // 任务ID
    private String taskName; // 任务名称
    private String executionId; // 执行ID
    private String starterName; // 任务发起人
    private String assigneeName; // 任务执行人
    private String processDefinitionName; // 流程定义标识
    private String description; // 任务描述
    private String dueTime; // 任务到期时间
    private String endTime; // 任务结束时间
}
