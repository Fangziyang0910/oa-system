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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/operator")
@Api(description = "操作人权限")
public class OperatorController {
    @Autowired
    private OperatorService operatorService;
    
    @ApiOperation("操作人查询所有操作任务")
    @GetMapping("/listOperatorTasks")
    public List<TaskVo> listOperatorTasks() {
        return operatorService.listOperatorTasks();
    }

    @ApiOperation("操作人查询选中任务的申请工单")
    @GetMapping("/getStartForm/{taskId}")
    public FormVo getStartForm(
        @PathVariable(value = "taskId") String taskId
    ) {
        return operatorService.getStartForm(taskId);
    }

    @ApiOperation("操作人查询审批工单模板")
    @GetMapping("/getTaskForm/{taskId}")
    public FormVo getTaskForm(
        @PathVariable(value = "taskId") String taskId
    ) {
        return operatorService.getTaskForm(taskId);
    }

    @ApiOperation("操作人提交审批工单")
    @PostMapping("/completeOperatorTask")
    public void completeOperatorTask(
        @RequestBody @Validated FormParam form
    ) { 
        operatorService.finishOperatorTask(form.getTaskId(), form.getForm());
    }

    @ApiOperation("操作人查询历史审批任务")
    @GetMapping("/listHistoricalTasks")
    public OperatorVo listHistoricalTasks() {
        Long operatorId = UserContext.getCurrentUserId();
        return operatorService.selectByOperatorId(operatorId);
    }

    @ApiOperation("操作人查询历史审批工单详情")
    @GetMapping("/getHistoricalDetails/{taskId}")
    public TaskVo getHistoricalDetails(
        @PathVariable(value = "taskId") String taskId
    ) { 
        return operatorService.getHistoricalDetails(taskId);
    }
}
