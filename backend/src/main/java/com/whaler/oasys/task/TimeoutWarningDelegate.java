package com.whaler.oasys.task;

import java.text.SimpleDateFormat;
import java.util.Date;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Task task : tasks) {
            String userName = task.getAssignee();
            System.out.println("超时提醒给："+userName);
            if (userName==null) {
                continue;
            }    
            String msgName="任务超时提醒";
            String msgContent="";
            Date date=task.getDueDate();
            if (date==null) {
                msgContent="任务 "+task.getName()+" 即将超时，请及时处理!";
            }else{
                String time= dateFormat.format(date);
                msgContent="任务 "+task.getName()+" 将于 "+time+" ，请及时处理!";
            }
            myMesgSender.sendMessage(userName,msgName,msgContent);
        }
    }
}
