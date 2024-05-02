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

@RestController
@RequestMapping("/approver")
public class ApproverController {
    @Autowired
    private ApproverService approverService;

    @GetMapping("/listApprovalTasks")
    public List<TaskVo> listApprovalTasks() {
        return approverService.listApprovalTasks();
    }

    @GetMapping("/getStartForm/{taskId}")
    public FormVo getStartForm(
        @PathVariable(value = "taskId") String taskId
    ){
        return approverService.getStartForm(taskId);
    }

    @GetMapping("/getTaskForm/{taskId}")
    public FormVo getTaskForm(
        @PathVariable(value = "taskId") String taskId
    ){
        return approverService.getTaskForm(taskId);
    }

    @PostMapping("/completeApprovalTask")
    public void completeApprovalTask(
        @RequestBody @Validated FormParam formParam
    ){
        approverService.finishApprovalTask(formParam.getTaskId(), formParam.getForm());
    }
    
    @GetMapping("/listHistoricalTasks")
    public ApproverVo listHistoricalTasks() {
        Long approverId = UserContext.getCurrentUserId();
        return approverService.selectByApproverId(approverId);
    }

    @GetMapping("/getHistoricalDetails/{taskId}")
    public TaskVo getHistoricalDetails(
        @PathVariable(value = "taskId") String taskId
    ){ 
        return approverService.getHistoricalDetails(taskId);
    }
}
