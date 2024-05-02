package com.whaler.oasys.model.param;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Valid
@Accessors(chain = true)
public class FormParam {
    @NotNull(message = "任务id不能为空")
    private String taskId;
    @NotNull(message = "表单不能为空")
    private Map<String,String>form;
}
