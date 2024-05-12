package com.whaler.oasys.controller.api;

import java.util.List;
import java.util.stream.Collectors;

import org.flowable.engine.HistoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.FormParam;
import com.whaler.oasys.model.vo.ApproverVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;
import com.whaler.oasys.service.ApproverService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/approver")
@Api(description = "审批人权限")
public class ApproverController {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ApproverService approverService;
    @Autowired
    private ApplicantService applicantService;

    @ApiOperation("申请人查询未完成的审批任务")
    @GetMapping("/listApprovalTasksNotCompleted")
    public List<TaskVo> listApprovalTasksNotCompleted() {
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
    public String getTaskForm(
        @PathVariable(value = "taskId") String taskId
    ){
        return approverService.getTaskForm(taskId);
    }

    @ApiOperation("申请人提交审批工单")
    @PostMapping("/completeApprovalTask")
    public void completeApprovalTask(
        @RequestBody @Validated FormParam formParam
    ){
        approverService.completeApprovalTask(formParam.getTaskId(), formParam.getForm());
    }
    
    @ApiOperation("申请人查询已完成的审批任务")
    @GetMapping("/listApprovalTasksCompleted")
    public List<TaskVo> listApprovalTasksCompleted() {
        Long approverId = UserContext.getCurrentUserId();
        ApproverVo approverVo = approverService.selectByApproverId(approverId);
        List<TaskVo> taskVos = approverVo.getTaskIds().stream()
            .map(taskId->approverService.getHistoricalDetails(taskId))
            .collect(Collectors.toList());
        return taskVos;
    }

    @ApiOperation("申请人查询已完成的单个审批任务")
    @GetMapping("/getTaskCompleted/{taskId}")
    public TaskVo getTaskCompleted(
        @PathVariable(value = "taskId") String taskId
    ){ 
        return approverService.getHistoricalDetails(taskId);
    }

    @ApiOperation("申请人查询已提交的任务表单")
    @GetMapping("/getHistoricalForm/{taskId}")
    public FormVo getHistoricalForm(
        @PathVariable(value = "taskId") String taskId
    ){ 
        FormVo formVo = applicantService.getHistoricalForm(taskId);
        return formVo;
    }

    @ApiOperation("申请人查询任务的流程进度")
    @GetMapping("/getProcessProgress/{taskId}")
    public List<TaskVo> getProcessInstance(
        @PathVariable(value = "taskId") String taskId
    ){ 
        HistoricTaskInstance task= historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        String processInstanceId = task.getProcessInstanceId();
        List<TaskVo>taskVos=applicantService.getProcessInstanceProgress(processInstanceId);
        return taskVos;
    }
}
