package com.whaler.oasys.task;

import java.util.List;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.config.BeanDefinition;

import com.alibaba.fastjson.JSON;

/**
 * AbortExecutionListener类是一个执行监听器，用于在流程审批不通过时更新流程进度。
 * 它实现了ExecutionListener接口，以提供在特定流程事件发生时执行自定义逻辑的能力。
 * 这个类被标记为@Component，表示它是一个Spring Bean，且其作用域为SCOPE_PROTOTYPE，即每次请求都创建一个新的实例。
 */
@Component("AbortExecutionListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AbortExecutionListener 
implements ExecutionListener {
    // 通过自动化装配获取RuntimeService实例，用于与流程引擎交互
    @Autowired
    private RuntimeService runtimeService;

    /**
     * 当监听的事件发生时，执行这个方法来处理逻辑。
     * 
     * @param execution 代表当前执行流程实例的DelegateExecution对象，提供访问和修改流程变量的能力。
     */
    @Override
    public void notify(DelegateExecution execution) {
        // 获取并更新流程中的表单列表变量，添加“审批不通过”条目
        String jsonString=(String)runtimeService.getVariable(execution.getId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add("审批不通过");
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(execution.getId(), "formList", jsonString);
    }
}

