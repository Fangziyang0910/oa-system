package com.whaler.oasys.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.param.FormParam;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.OperatorVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.OperatorService;

@RestController
@RequestMapping("/operator")
public class OperatorController {
    @Autowired
    private OperatorService operatorService;
    
    @GetMapping("/listOperatorTasks")
    public List<TaskVo> listOperatorTasks() {
        return operatorService.listOperatorTasks();
    }

    @GetMapping("/getStartForm/{taskId}")
    public FormVo getStartForm(
        @PathVariable(value = "taskId") String taskId
    ) {
        return operatorService.getStartForm(taskId);
    }

    @GetMapping("/getTaskForm/{taskId}")
    public FormVo getTaskForm(
        @PathVariable(value = "taskId") String taskId
    ) {
        return operatorService.getTaskForm(taskId);
    }

    @PostMapping("/completeOperatorTask")
    public void completeOperatorTask(
        @RequestBody @Validated FormParam form
    ) { 
        operatorService.finishOperatorTask(form.getTaskId(), form.getForm());
    }

    @GetMapping("/listHistoricalTasks")
    public OperatorVo listHistoricalTasks() {
        Long operatorId = UserContext.getCurrentUserId();
        return operatorService.selectByOperatorId(operatorId);
    }

    @GetMapping("/getHistoricalDetails/{taskId}")
    public TaskVo getHistoricalDetails(
        @PathVariable(value = "taskId") String taskId
    ) { 
        return operatorService.getHistoricalDetails(taskId);
    }
}
