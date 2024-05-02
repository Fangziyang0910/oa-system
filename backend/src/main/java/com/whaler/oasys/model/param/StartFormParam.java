package com.whaler.oasys.model.param;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Valid
@Accessors(chain = true)
public class StartFormParam {
    @NotNull(message = "流程实例id不能为空")
    private String processInstanceId;
    @NotNull(message = "表单不能为空")
    private Map<String,String>form;
    
}
