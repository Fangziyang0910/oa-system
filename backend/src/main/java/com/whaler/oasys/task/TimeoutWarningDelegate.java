package com.whaler.oasys.task;

import java.util.List;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.whaler.oasys.tool.MyMesgSender;

@Component("TimeoutWarningDelegate")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TimeoutWarningDelegate
implements JavaDelegate {
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private MyMesgSender myMesgSender;

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("超时提醒");
        String processInstanceId = execution.getProcessInstanceId();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        for (Task task : tasks) {
            String userName = task.getAssignee();
            System.out.println("超时提醒给："+userName);
            if (userName==null) {
                continue;
            }
            String msg="您的任务即将超时";
            myMesgSender.sendMessage(userName,msg);
        }
    }
}
