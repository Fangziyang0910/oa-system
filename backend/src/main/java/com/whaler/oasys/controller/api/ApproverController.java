package com.whaler.oasys.controller.api;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 审批人查询未完成的审批任务
     * 
     * @return 返回一个审批任务列表，这些任务尚未完成。
     */
    @GetMapping("/listApprovalTasksNotCompleted")
    public List<TaskVo> listApprovalTasksNotCompleted() {
        // 从审批服务中查询所有未完成的审批任务
        return approverService.listApprovalTasks();
    }

    
    /**
     * 审批人查询任务池中的任务
     * 该接口不接受任何参数，返回任务池中可供审批人选择的任务列表。
     * 
     * @return List<TaskVo> 返回一个任务列表，每个任务以TaskVo对象的形式表示。
     */
    @ApiOperation("审批人查询任务池中的任务")
    @GetMapping("/listApprovalCandidateTasks")
    public List<TaskVo> listApprovalCandidateTasks() {
        // 从服务层获取审批候选人的任务列表
        return approverService.listApprovalCandidateTasks();
    }

    /**
     * 审批人申领任务池中的任务。
     * <p>该接口用于让审批人从任务池中申领特定的任务。通过提供任务ID，审批人可以将任务从池中认领到自己名下，开始进行处理。
     *
     * @param taskId 任务的ID，用于标识要申领的具体任务。这是一个必传参数，通过URL路径变量传递。
     * @see approverService.claimCandidateTask(String) 对应的服务层方法，用于实际执行任务的申领操作。
     */
    @ApiOperation("审批人申领任务池中的任务")
    @GetMapping("/claimCandidateTask/{taskId}")
    public void claimCandidateTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        approverService.claimCandidateTask(taskId); // 调用服务层方法，实现任务的申领
    }

    /**
     * 审批人放弃申领任务的操作接口。
     * 该接口通过任务ID，将指定的任务从审批人候选列表中移除，即放弃申领该任务。
     * 
     * @param taskId 任务的唯一标识符，通过路径变量传递。
     *               该参数用于指定要放弃申领的具体任务。
     * @return 该接口不返回任何内容。
     */
    @ApiOperation("审批人放弃申领任务")
    @GetMapping("/unclaimCandidateTask/{taskId}")
    public void unclaimCandidateTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        // 调用服务层方法，实现审批人放弃申领任务的逻辑
        approverService.unclaimCandidateTask(taskId);
    }

    /**
     * 审批人查询受理的任务
     * 
     * @return 返回一个任务列表，该列表包含了审批人当前被分配需要受理的任务。
     */
    @GetMapping("/listApprovalAssignTasks")
    public List<TaskVo> listApprovalAssignTasks() {
        // 调用服务层方法，查询并返回审批人需要受理的任务列表
        return approverService.listApprovalAssignTasks();
    }
    
    /**
     * 审批人查询未完成任务的详细信息。
     * 
     * 通过指定的任务ID，查询该任务的详细状态和信息，仅包括未完成的任务。
     * 
     * @param taskId 审批任务的唯一标识符，通过路径变量传递。
     * @return 返回一个TaskVo对象，包含未完成任务的详细信息。
     */
    @ApiOperation("审批人查询未完成任务详情")
    @GetMapping("/getTaskNotCompleted/{taskId}")
    public TaskVo getTaskNotCompleted(
        @PathVariable("taskId") String taskId
    ) {
        // 调用服务层方法，查询指定ID的未完成任务详情
        return approverService.getTaskNotCompleted(taskId);
    }

    /**
     * 审批人查询选中任务的申请工单
     * 
     * @param taskId 任务的ID，作为查询申请工单的依据。
     * @return FormVo 返回查询到的申请工单信息，封装在FormVo对象中。
     */
    @ApiOperation("审批人查询选中任务的申请工单")
    @GetMapping("/getStartForm/{taskId}")
    public FormVo getStartForm(
        @PathVariable(value = "taskId") String taskId
    ){
        // 通过任务ID查询并返回申请工单信息
        return approverService.getStartForm(taskId);
    }

    /**
     * 根据任务ID查询缓存的任务工单表单数据
     * 
     * @param taskId 任务的唯一标识符，通过路径变量传递
     * @return 返回任务工单的表单数据，封装在FormVo对象中
     */
    @ApiOperation("审批人查询缓存的任务工单")
    @GetMapping("/getTaskFormData/{taskId}")
    public FormVo getTaskFormData(
        @PathVariable(value = "taskId") String taskId
    ){
        // 调用服务层方法，根据任务ID获取任务工单的表单数据
        return approverService.getTaskFormData(taskId);
    }

    /**
     * 根据审批人的查询需求，获取指定审批工单的模板。
     * 
     * @param taskId 审批工单的唯一标识符，通过URL路径变量传递。
     * @return 返回审批工单对应的模板内容，类型为String。
     */
    @ApiOperation("审批人查询审批工单模板")
    @GetMapping("/getTaskForm/{taskId}")
    public String getTaskForm(
        @PathVariable(value = "taskId") String taskId
    ){
        // 通过审批服务接口获取指定任务表单
        return approverService.getTaskForm(taskId);
    }

    /**
     * 审批人缓存任务工单。
     * 该接口用于保存审批任务的相关信息到缓存中。
     * 
     * @param formParam 包含审批任务ID和表单数据的请求体参数。
     *                  - taskId: 审批任务的唯一标识符。
     *                  - form: 表单数据，包含了与审批任务相关的信息。
     * @apiNote 该方法不返回任何内容。
     */
    @PostMapping("/saveApprovalTask")
    public void saveApprovalTask(
        @RequestBody @Validated FormParam formParam
    ){
        // 将审批任务信息保存到服务中
        approverService.saveApprovalTask(formParam.getTaskId(), formParam.getForm());
    }

    /**
     * 完成审批人自己的任务。
     * <p>
     * 该接口用于审批人结束自己正在受理的任务。通过提供任务ID，调用审批服务，
     * 对指定的任务进行结束操作。
     *
     * @param taskId 任务ID，用于标识需要结束的任务。
     *               该ID作为URL路径变量传递给函数。
     * @see approverService#completeApprovalOwnTask(String) 完成审批任务的具体实现。
     */
    @ApiOperation("审批人结束受理任务")
    @GetMapping("/completeApprovalTask/{taskId}")
    public void completeApprovalOwnTask(
        @PathVariable(value = "taskId") String taskId
    ){
        // 调用服务层方法，完成审批人自己的任务
        approverService.completeApprovalOwnTask(taskId);
    }

    /**
     * 提交审批工单的接口。
     * 该接口允许审批人完成对应的审批任务。
     *
     * @param formParam 审批表单参数，包含需要完成的审批任务ID和审批表单内容。
     *                  需要进行合法性验证以确保数据的正确性。
     * @apiNote 该接口不返回任何内容，而是通过调用服务层方法来完成审批任务的处理。
     */
    @PostMapping("/completeApprovalTask")
    public void completeApprovalTask(
        @RequestBody @Validated FormParam formParam
    ){
        // 调用服务层方法，完成指定的审批任务
        approverService.completeApprovalTask(formParam.getTaskId(), formParam.getForm());
    }
    
    /**
     * 审批人查询已完成的受理任务
     * 该接口不接受任何参数，返回审批人已完成的任务列表。
     * 
     * @return 返回类型为List<TaskVo>，表示审批人已完成的任务列表。
     */
    @ApiOperation("审批人查询已完成的受理任务")
    @GetMapping("/listApprovalTasksCompleted")
    public List<TaskVo> listApprovalTasksCompleted() {
        // 获取当前用户的ID
        Long approverId = UserContext.getCurrentUserId();
        // 根据审批人ID查询审批人信息
        ApproverVo approverVo = approverService.selectByApproverId(approverId);
        List<TaskVo> taskVos = new ArrayList<>();
        
        // 遍历审批人任务ID列表，获取每个任务的详细信息
        approverVo.getTaskIds().forEach(
            taskId->{
                try{
                    // 尝试获取任务的历史详情，并添加到结果列表中
                    taskVos.add(approverService.getHistoricalDetails(taskId));
                }catch(Exception e){
                    // 如果获取失败，则跳过该任务，不添加到结果列表中
                    return;
                }
            }
        );
        return taskVos;
    }

    /**
     * 根据任务ID查询已完成的单个审批任务。
     * 
     * @param taskId 审批任务的唯一标识符，通过路径变量传递。
     * @return 返回一个审批任务的详细信息对象（TaskVo）。
     */
    @ApiOperation("审批人查询已完成的单个审批任务")
    @GetMapping("/getTaskCompleted/{taskId}")
    public TaskVo getTaskCompleted(
        @PathVariable(value = "taskId") String taskId
    ){ 
        // 调用服务层方法，获取指定任务ID的已完成任务详情
        return approverService.getHistoricalDetails(taskId);
    }

    /**
     * 审批人查询已提交的任务表单
     * 
     * @param taskId 任务的ID，作为查询已提交表单的依据。
     * @return 返回一个FormVo对象，包含查询到的表单信息。
     */
    @ApiOperation("审批人查询已提交的任务表单")
    @GetMapping("/getHistoricalForm/{taskId}")
    public FormVo getHistoricalForm(
        @PathVariable(value = "taskId") String taskId
    ){ 
        // 通过申请人服务查询指定任务ID的表单历史记录
        FormVo formVo = applicantService.getHistoricalForm(taskId);
        return formVo;
    }

    /**
     * 审批人查询任务的流程进度
     * 
     * @param taskId 任务的唯一标识符
     * @return 返回任务流程进度的列表
     * @throws ApiException 如果指定的任务不存在，则抛出异常
     */
    @ApiOperation("审批人查询任务的流程进度")
    @GetMapping("/getProcessProgress/{taskId}")
    public List<TaskVo> getProcessInstance(
        @PathVariable(value = "taskId") String taskId
    ){ 
        // 根据任务ID查询历史任务实例
        HistoricTaskInstance task= historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();
                
        // 如果任务不存在，则抛出异常
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        
        // 获取任务对应的流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        
        // 根据流程实例ID查询流程进度信息
        List<TaskVo>taskVos=applicantService.getProcessInstanceProgress(processInstanceId);
        
        return taskVos;
    }
}
