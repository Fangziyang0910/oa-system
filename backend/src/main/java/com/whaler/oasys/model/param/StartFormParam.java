package com.whaler.oasys.model.param;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * StartFormParam 类用于封装启动表单的参数
 * @param processInstanceId 流程实例id
 * @param form 表单数据
 */
@Data
@Valid
@Accessors(chain = true)
public class StartFormParam {
    // 流程实例id，不能为空
    @NotNull(message = "流程实例id不能为空")
    private String processInstanceId;
    
    // 表单数据，不能为空
    @NotNull(message = "表单不能为空")
    private Map<String,String>form;
    
}
