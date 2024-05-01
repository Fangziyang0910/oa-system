package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务视图对象类，用于封装任务的相关信息。
 * @param taskId 任务ID
 * @param taskName 任务名称
 * @param executionId 执行ID
 * @param description 任务描述
 * @param endTime 任务结束时间
 */
@Data
@Accessors(chain = true)
public class TaskVo {
    private String taskId; // 任务ID
    private String taskName; // 任务名称
    private String executionId; // 执行ID
    private String description; // 任务描述
    private String endTime; // 任务结束时间
}
