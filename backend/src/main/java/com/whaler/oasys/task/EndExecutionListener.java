package com.whaler.oasys.task;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import java.util.List;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * 用于结束执行的监听器组件。
 * 实现了ExecutionListener接口，用于监听流程的结束节点。
 */
@Component("EndExecutionListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class EndExecutionListener
implements ExecutionListener {
    // 自动注入RuntimeService，用于操作流程实例
    @Autowired
    private RuntimeService runtimeService;
    
    /**
     * 当监听的事件发生时，执行此方法。
     * 更新流程实例中的表单列表变量，添加“流程结束”条目。
     * 
     * @param execution 代表当前执行流程实例的DelegateExecution对象。
     */
    @Override
    public void notify(DelegateExecution execution) {
        // 获取并更新流程中的表单列表变量
        String jsonString=(String)runtimeService.getVariable(execution.getId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add("流程结束");
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(execution.getId(), "formList", jsonString);
    }
}
