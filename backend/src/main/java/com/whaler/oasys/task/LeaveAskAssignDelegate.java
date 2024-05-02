package com.whaler.oasys.task;

import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.whaler.oasys.model.vo.CategoryVo;
import com.whaler.oasys.service.CategoryService;

@Component
public class LeaveAskAssignDelegate
implements JavaDelegate{
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TaskService taskService;

    @Override
    public void execute(DelegateExecution execution){
        Task task;
    }
}
