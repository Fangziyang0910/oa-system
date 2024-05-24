package com.whaler.oasys.task;

import java.util.Date;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 一个实现TaskListener接口的组件，用于监听任务超时警告。
 * @Component注解指明了该类是一个Spring Bean，Bean的名称为"TimeoutWarningListener"。
 * @Scope注解指明了该Bean的作用域为原型（即每次请求都创建一个新的实例）。
 */
@Component("TimeoutWarningListener")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TimeoutWarningListener
implements TaskListener {
    /**
     * 当监听的任务发生事件时，此方法会被调用。
     * @param task 代表被监听的任务的DelegateTask对象。
     */
    @Override
    public void notify(DelegateTask task) {
        System.out.println("任务监听器");
        Date date=task.getDueDate(); // 获取任务的截止日期
        // 获得当前时间
        Date now=new Date();
        // 计算任务截止时间和当前时间之间的时间差，并将其转换为睡眠时间（四分之三的时间差）
        Long diff=(date.getTime()-now.getTime())*4/5;
        try {
            Thread.sleep(diff); // 根据计算出的时间差让线程睡眠，模拟等待任务超时
        }catch (Exception e) {
            e.printStackTrace();
        }
        String assignee=task.getAssignee(); // 获取任务的当前分配者
        if (assignee==null) {
            System.out.println("任务未签收");
        }
        System.out.println("任务签收人："+assignee);
    }
}
