package com.whaler.oasys.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.whaler.oasys.tool.MyMesgSender;

/**
 * TimeoutWarningDelegate类是一个处理任务超时警告的组件。
 * 它通过实现JavaDelegate接口，提供了一个execute方法来执行具体的逻辑。
 * 这个类被标记为@Component，名称为"TimeoutWarningDelegate"，并设置作用域为SCOPE_PROTOTYPE，表示每个请求都会创建一个新的实例。
 */
@Component("TimeoutWarningDelegate")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TimeoutWarningDelegate
implements JavaDelegate {
    // 通过@Autowired自动注入TaskService和MyMesgSender依赖
    @Autowired
    private TaskService taskService;
    @Autowired
    private MyMesgSender myMesgSender;

    /**
     * 执行超时提醒逻辑。
     * @param execution 代表当前流程执行实例的DelegateExecution对象。
     */
    @Override
    public void execute(DelegateExecution execution) {
        // 打印超时提醒日志
        System.out.println("超时提醒");
        // 获取当前流程实例ID
        String processInstanceId = execution.getProcessInstanceId();
        // 根据流程实例ID查询当前所有未完成的任务
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        // 初始化日期格式化工具
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 遍历所有任务，为每个任务发送超时提醒
        for (Task task : tasks) {
            // 获取任务分配的用户
            String userName = task.getAssignee();
            // 打印提醒接收者日志
            System.out.println("超时提醒给："+userName);
            // 如果任务未分配给任何用户，则跳过当前循环
            if (userName==null) {
                continue;
            }    
            // 初始化提醒消息内容
            String msgName="任务超时提醒";
            String msgContent="";
            // 获取任务的截止日期
            Date date=task.getDueDate();
            // 根据任务是否设置截止日期，拼接不同的提醒内容
            if (date==null) {
                msgContent="任务 "+task.getName()+" 即将超时，请及时处理!";
            }else{
                String time= dateFormat.format(date);
                msgContent="任务 "+task.getName()+" 将于 "+time+" ，请及时处理!";
            }
            // 发送提醒消息给任务分配用户
            myMesgSender.sendMessage(userName,msgName,msgContent);
        }
    }
}
