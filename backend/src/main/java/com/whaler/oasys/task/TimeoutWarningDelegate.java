package com.whaler.oasys.task;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("TimeoutWarningDelegate")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TimeoutWarningDelegate
implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("超时提醒");
    }
}
