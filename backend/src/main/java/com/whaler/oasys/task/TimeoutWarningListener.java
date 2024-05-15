package com.whaler.oasys.task;

import java.util.Date;
import java.util.List;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.api.Task;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component("TimeoutWarningListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TimeoutWarningListener
implements TaskListener {
    @Autowired
    private TaskService taskService;
    @Override
    public void notify(DelegateTask task) {
        System.out.println("任务监听器");
        Date date=task.getDueDate();
        // 获得当前时间
        Date now=new Date();
        // 计算时间差
        Long diff=(date.getTime()-now.getTime())*4/5;
        try {
            Thread.sleep(diff);
        }catch (Exception e) {
            e.printStackTrace();
        }
        String assignee=task.getAssignee();
        if (assignee==null) {
            System.out.println("任务未签收");
        }
        System.out.println("任务签收人："+assignee);
    }
}
