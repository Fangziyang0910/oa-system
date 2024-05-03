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
import com.whaler.oasys.model.vo.ApproverVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApproverService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/approver")
@Api(description = "审批人权限")
public class ApproverController {
    @Autowired
    private ApproverService approverService;

    @ApiOperation("申请人查询所有审批任务")
    @GetMapping("/listApprovalTasks")
    public List<TaskVo> listApprovalTasks() {
        return approverService.listApprovalTasks();
    }

    @ApiOperation("申请人查询选中任务的申请工单")
    @GetMapping("/getStartForm/{taskId}")
    public FormVo getStartForm(
        @PathVariable(value = "taskId") String taskId
    ){
        return approverService.getStartForm(taskId);
    }

    @ApiOperation("申请人查询审批工单模板")
    @GetMapping("/getTaskForm/{taskId}")
    public FormVo getTaskForm(
        @PathVariable(value = "taskId") String taskId
    ){
        return approverService.getTaskForm(taskId);
    }

    @ApiOperation("申请人提交审批工单")
    @PostMapping("/completeApprovalTask")
    public void completeApprovalTask(
        @RequestBody @Validated FormParam formParam
    ){
        approverService.finishApprovalTask(formParam.getTaskId(), formParam.getForm());
    }
    
    @ApiOperation("申请人查询历史审批任务")
    @GetMapping("/listHistoricalTasks")
    public ApproverVo listHistoricalTasks() {
        Long approverId = UserContext.getCurrentUserId();
        return approverService.selectByApproverId(approverId);
    }

    @ApiOperation("申请人查询历史审批工单详情")
    @GetMapping("/getHistoricalDetails/{taskId}")
    public TaskVo getHistoricalDetails(
        @PathVariable(value = "taskId") String taskId
    ){ 
        return approverService.getHistoricalDetails(taskId);
    }
}
