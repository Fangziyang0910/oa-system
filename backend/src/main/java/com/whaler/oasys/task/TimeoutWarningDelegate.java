package com.whaler.oasys.task;

import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("TimeoutWarningDelegate")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TimeoutWarningDelegate
implements JavaDelegate {
    @Autowired
    private TaskService taskService;
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("超时提醒");
        DelegateExecution parentExecution= execution.getParent();
        if(parentExecution==null){
            System.out.println("父流程实例空");
            return;
        }
        String parentExecutionId = parentExecution.getId();
        System.out.println("父流程实例ID：" + parentExecutionId);
        Task task = taskService.createTaskQuery().executionId(parentExecutionId)
            .singleResult();
        if (task == null) {
            System.out.println("用户任务不存在");
            return;
        }

        String assign=task.getAssignee();
        
    }
}
