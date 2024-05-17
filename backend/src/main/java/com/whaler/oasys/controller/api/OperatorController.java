package com.whaler.oasys.controller.api;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.FormParam;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.OperatorVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;
import com.whaler.oasys.service.OperatorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/operator")
@Api(description = "操作人权限")
public class OperatorController {
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ApplicantService applicantService;
    @Autowired
    private HistoryService historyService;
    
    @ApiOperation("操作人查询未完成的审批任务")
    @GetMapping("/listOperatorTasksNotCompleted")
    public List<TaskVo> listOperatorTasksNotCompleted() {
        return operatorService.listOperatorTasks();
    }

    @ApiOperation("操作人查询任务池中的任务")
    @GetMapping("/listOperatorCandidateTasks")
    public List<TaskVo> listOperatorCandidateTasks() {
        return operatorService.listOperatorCandidateTasks();
    }

    @ApiOperation("操作人申领任务池中的任务")
    @GetMapping("/claimCandidateTask/{taskId}")
    public void claimCandidateTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        operatorService.claimCandidateTask(taskId);
    }

    @ApiOperation("操作人放弃申领任务")
    @GetMapping("/unclaimCandidateTask/{taskId}")
    public void unclaimCandidateTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        operatorService.unclaimCandidateTask(taskId);
    }

    @ApiOperation("操作人查询受理和被委托的任务")
    @GetMapping("/listOperatorAssignTasks")
    public List<TaskVo> listOperatorAssignTasks() {
        return operatorService.listOperatorAssignTasks();
    }
    
    @ApiOperation("操作人查询未完成任务详情")
    @GetMapping("/getTaskNotCompleted/{taskId}")
    public TaskVo getTaskNotCompleted(
        @PathVariable("taskId") String taskId
    ) {
        return operatorService.getTaskNotCompleted(taskId);
    }

    @ApiOperation("操作人查询委派候选人")
    @GetMapping("/listOperatorCandidateUsers/{taskId}")
    public List<String> listOperatorCandidateUsers(
        @PathVariable(value = "taskId") String taskId
    ) {
        return operatorService.listOperatorCandidateUsers(taskId);
    }

    @ApiOperation("操作人委派受理任务")
    @PostMapping("/assignTask")
    public void assignTask(
        @RequestParam(value = "taskId") String taskId,
        @RequestParam(value = "userName") String userName
    ) {
       operatorService.assignTask(taskId, userName); 
    }

    @ApiOperation("操作人取消委派受理任务")
    @GetMapping("/unassignTask/{taskId}")
    public void unassignTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        operatorService.unassignTask(taskId);
    }

    @ApiOperation("操作人查询选中任务的申请工单")
    @GetMapping("/getStartForm/{taskId}")
    public FormVo getStartForm(
        @PathVariable(value = "taskId") String taskId
    ) {
        return operatorService.getStartForm(taskId);
    }

    @ApiOperation("操作人查询缓存的任务工单")
    @GetMapping("/getTaskFormData/{taskId}")
    public FormVo getTaskFormData(
        @PathVariable(value = "taskId") String taskId
    ) {
        return operatorService.getTaskFormData(taskId);
    }

    @ApiOperation("操作人查询工单模板")
    @GetMapping("/getTaskForm/{taskId}")
    public String getTaskForm(
        @PathVariable(value = "taskId") String taskId
    ) {
        return operatorService.getTaskForm(taskId);
    }

    @ApiOperation("操作人缓存任务工单")
    @PostMapping("/saveOperatorTask")
    public void saveOperatorTask(
        @RequestBody @Validated FormParam form
    ) { 
        operatorService.saveOperatorTask(form.getTaskId(), form.getForm());
    }
    
    @ApiOperation("操作人结束委托任务")
    @GetMapping("/endAssignedTask/{taskId}")
    public void endAssignedTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        operatorService.endAssignedTask(taskId);
    }

    @ApiOperation("操作人结束受理任务")
    @GetMapping("/completeOperatorOwnTask/{taskId}")
    public void completeOperatorOwnTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        operatorService.completeOperatorOwnTask(taskId);
    }
    

    @ApiOperation("操作人提交审批工单")
    @PostMapping("/completeOperatorTask")
    public void completeOperatorTask(
        @RequestBody @Validated FormParam form
    ) { 
        operatorService.completeOperatorTask(form.getTaskId(), form.getForm());
    }

    @ApiOperation("操作人查询已完成的受理任务")
    @GetMapping("/listOperatorTasksCompleted")
    public List<TaskVo> listOperatorTasksCompleted() {
        Long operatorId = UserContext.getCurrentUserId();
        OperatorVo operatorVo = operatorService.selectByOperatorId(operatorId);
        List<TaskVo>taskVos= new ArrayList<>();
        operatorVo.getTaskIds().forEach(taskId -> {
            try{
                taskVos.add(operatorService.getHistoricalDetails(taskId));
            }catch(Exception e){
                return;
            }
        });
        return taskVos;
    }

    @ApiOperation("操作人查询已完成的单个任务")
    @GetMapping("/getTaskCompleted/{taskId}")
    public TaskVo getTaskCompleted(
        @PathVariable(value = "taskId") String taskId
    ) { 
        return operatorService.getHistoricalDetails(taskId);
    }

    @ApiOperation("操作人查询已提交的任务表单")
    @GetMapping("/getHistoricalForm/{taskId}")
    public FormVo getHistoricalForm(
        @PathVariable(value = "taskId") String taskId
    ){ 
        FormVo formVo = applicantService.getHistoricalForm(taskId);
        return formVo;
    }

    @ApiOperation("操作人查询任务的流程进度")
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
