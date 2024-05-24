package com.whaler.oasys.model.param;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * FormParam类用于封装表单参数。
 * 它包含了一个任务ID和一个表单数据的映射。
 * @param taskId 任务ID
 * @param form 表单数据，以键值对形式存储
 */
@Data
@Valid
@Accessors(chain = true)
public class FormParam {
    // 任务ID，不能为空
    @NotNull(message = "任务id不能为空")
    private String taskId;
    
    // 表单数据，以键值对形式存储，不能为空
    @NotNull(message = "表单不能为空")
    private Map<String,String> form;
}
