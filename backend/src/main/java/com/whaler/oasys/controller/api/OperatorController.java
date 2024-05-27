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
    
    /**
     * 操作人查询未完成的审批任务
     * 
     * @return 返回一个任务列表，包含操作人尚未完成的审批任务
     */
    @ApiOperation("操作人查询未完成的审批任务")
    @GetMapping("/listOperatorTasksNotCompleted")
    public List<TaskVo> listOperatorTasksNotCompleted() {
        // 从操作人服务中获取未完成的审批任务列表
        return operatorService.listOperatorTasks();
    }

    /**
     * 操作人查询任务池中的任务
     * 本接口不需要接收任何参数，调用后将返回任务池中可供操作人选择的任务列表。
     * 
     * @return 返回一个任务列表，列表中的每个元素都是TaskVo类型，代表一个可选任务。
     */
    @ApiOperation("操作人查询任务池中的任务")
    @GetMapping("/listOperatorCandidateTasks")
    public List<TaskVo> listOperatorCandidateTasks() {
        // 从操作人服务中获取操作人可领取的任务列表
        return operatorService.listOperatorCandidateTasks();
    }

    /**
     * 操作人申领任务池中的任务。
     * <p>
     * 该接口通过获取任务的ID，将指定的任务从任务池中申领给操作人。
     * 申领后，任务将与操作人绑定，其他人无法再申领该任务。
     *
     * @param taskId 任务的ID，用于指定要申领的任务。
     *               该ID通过URL路径变量传递给方法。
     * @see OperatorService#claimCandidateTask(String) 用于实际执行申领任务的逻辑。
     */
    @ApiOperation("操作人申领任务池中的任务")
    @GetMapping("/claimCandidateTask/{taskId}")
    public void claimCandidateTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        operatorService.claimCandidateTask(taskId); // 调用服务层方法，实现任务的申领
    }

    /**
     * 操作人放弃申领任务
     * 
     * 该接口用于让操作人主动放弃领取某个任务。任务的标识由URL中的taskId参数指定。
     * 
     * @param taskId 任务的唯一标识符。该参数通过URL路径传递。
     * 
     * 注意：该方法没有返回值，操作结果直接通过服务调用进行处理。
     */
    @ApiOperation("操作人放弃申领任务")
    @GetMapping("/unclaimCandidateTask/{taskId}")
    public void unclaimCandidateTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        // 调用服务层方法，实现放弃申领任务的逻辑
        operatorService.unclaimCandidateTask(taskId);
    }

    /**
     * 操作人查询受理和被委托的任务。
     * <p>
     * 本接口无需参数，调用后将返回操作人当前受理和被委托的所有任务列表。
     *
     * @return 返回一个任务列表，列表中每个元素都代表一个任务的详细信息。
     */
    @ApiOperation("操作人查询受理和被委托的任务")
    @GetMapping("/listOperatorAssignTasks")
    public List<TaskVo> listOperatorAssignTasks() {
        // 从操作人服务中获取操作人当前受理和被委托的所有任务
        return operatorService.listOperatorAssignTasks();
    }
        
    /**
     * 根据任务ID查询未完成任务的详细信息。
     * <p>
     * 该接口通过任务ID来查询特定未完成任务的详细状况，便于操作人员掌握任务进度和详情。
     *
     * @param taskId 任务的唯一标识符，通过路径变量传递。
     * @return 返回未完成任务的详细信息对象TaskVo。
     */
    @ApiOperation("操作人查询未完成任务详情")
    @GetMapping("/getTaskNotCompleted/{taskId}")
    public TaskVo getTaskNotCompleted(
            @PathVariable("taskId") String taskId
    ) {
        // 调用服务层方法，查询指定ID的未完成任务详情
        return operatorService.getTaskNotCompleted(taskId);
    }

    /**
     * 操作人查询委派候选人
     * 
     * @param taskId 任务的唯一标识符，通过路径变量传递。
     * @return 返回一个字符串列表，包含可以被委派为操作人的候选用户ID。
     */
    @ApiOperation("操作人查询委派候选人")
    @GetMapping("/listOperatorCandidateUsers/{taskId}")
    public List<String> listOperatorCandidateUsers(
        @PathVariable(value = "taskId") String taskId
    ) {
        // 调用服务层方法，查询指定任务ID的操作人候选用户
        return operatorService.listOperatorCandidateUsers(taskId);
    }

    /**
     * 操作人委派受理任务。
     * 该接口用于将指定的任务分配给指定的操作人。
     *
     * @param taskId 任务ID，用于指定需要分配的任务。
     * @param userName 操作人用户名，指定任务的受理人。
     * 
     * @apiNote 该方法不返回任何内容。
     */
    @PostMapping("/assignTask")
    public void assignTask(
        @RequestParam(value = "taskId") String taskId,
        @RequestParam(value = "userName") String userName
    ) {
    // 委派任务给指定的操作人
    operatorService.assignTask(taskId, userName); 
    }

    /**
     * 操作人取消委派受理任务。
     * <p>该接口用于取消指定的任务分配，将任务从当前操作人手中释放。</p>
     *
     * @param taskId 需要取消分配的任务的ID，类型为String。
     *               <p>该参数通过URL路径变量传递，用于指定要取消分配的具体任务。</p>
     * @see OperatorService#unassignTask(String) 对应的服务层方法，用于实现取消任务分配的业务逻辑。
     */
    @ApiOperation("操作人取消委派受理任务")
    @GetMapping("/unassignTask/{taskId}")
    public void unassignTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        operatorService.unassignTask(taskId); // 调用服务层方法，实现任务取消分配
    }

    /**
     * 根据任务ID查询发起该任务的工单信息。
     * <p>
     * 该接口用于查询特定任务的发起工单详情。任务ID是查询的关键参数。
     *
     * @param taskId 任务的唯一标识符，通过路径变量传递。
     * @return FormVo 返回工单的详细信息，封装在FormVo对象中。
     */
    @ApiOperation("操作人查询选中任务的申请工单")
    @GetMapping("/getStartForm/{taskId}")
    public FormVo getStartForm(
        @PathVariable(value = "taskId") String taskId
    ) {
        // 调用operatorService中的getStartForm方法，根据taskId获取相应的工单信息
        return operatorService.getStartForm(taskId);
    }

    /**
     * 根据任务工单ID查询缓存中的任务工单表单数据。
     * 
     * @param taskId 任务工单的唯一标识符。
     * @return 返回任务工单的表单数据，封装在FormVo对象中。
     */
    @ApiOperation("操作人查询缓存的任务工单")
    @GetMapping("/getTaskFormData/{taskId}")
    public FormVo getTaskFormData(
        @PathVariable(value = "taskId") String taskId
    ) {
        // 调用operatorService服务，查询指定taskId的任务工单表单数据
        return operatorService.getTaskFormData(taskId);
    }

    /**
     * 根据操作人的查询请求获取指定工单模板的内容。
     * 
     * @param taskId 工单的唯一标识符，通过路径变量传递。
     * @return 返回工单模板的具体内容，类型为String。
     */
    @ApiOperation("操作人查询工单模板")
    @GetMapping("/getTaskForm/{taskId}")
    public String getTaskForm(
        @PathVariable(value = "taskId") String taskId
    ) {
        // 通过操作服务获取指定任务ID的工单表单
        return operatorService.getTaskForm(taskId);
    }

    /**
     * 操作人缓存任务工单。
     * 该接口用于保存操作人的任务工单信息到缓存中。
     * 
     * @param form 请求体参数，包含任务ID和表单数据。
     *   - @RequestBody 标注该参数为请求体中的内容。
     *   - @Validated 标注该参数已进行验证。
     * @apiOperation saveOperatorTask 操作人缓存任务工单的API操作。
     */
    @PostMapping("/saveOperatorTask")
    public void saveOperatorTask(
        @RequestBody @Validated FormParam form
    ) { 
        // 调用服务层方法，保存操作人任务工单信息
        operatorService.saveOperatorTask(form.getTaskId(), form.getForm());
    }
        
    /**
     * 结束指定的委托任务。
     * <p>
     * 该操作通过任务ID来标识特定的任务，并将其标记为结束状态。
     * 不返回任何内容，操作结果直接通过服务层方法实现，不涉及返回值处理。
     *
     * @param taskId 任务的唯一标识符，通过URL路径变量传递。
     *               用于在服务层中查找并结束对应的委托任务。
     */
    @ApiOperation("操作人结束委托任务")
    @GetMapping("/endAssignedTask/{taskId}")
    public void endAssignedTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        // 调用服务层方法，结束指定ID的委托任务
        operatorService.endAssignedTask(taskId);
    }

    /**
     * 结束操作人自己的任务。
     * <p>
     * 该接口用于操作人主动结束其正在受理的任务。通过提供任务ID，调用服务层方法完成任务的结束流程。
     * 
     * @param taskId 任务ID，用于标识需要结束的任务。这是一个路径变量，通过URL传递。
     * @return 该方法没有返回值。
     */
    @ApiOperation("操作人结束受理任务")
    @GetMapping("/completeOperatorOwnTask/{taskId}")
    public void completeOperatorOwnTask(
        @PathVariable(value = "taskId") String taskId
    ) {
        // 调用服务层方法，完成操作人自己的任务结束流程
        operatorService.completeOperatorOwnTask(taskId);
    }
        

    /**
     * 提交审批工单的操作人任务完成接口。
     * <p>
     * 该接口通过接收前端发送的表单数据，来完成操作人对应的审批工单任务。
     * 
     * @param form 表单参数对象，包含任务ID和表单数据。@RequestBody 用于解析请求体中的JSON数据，@Validated 用于验证表单数据的合法性。
     * @see FormParam 用于定义请求体的结构和数据类型。
     */
    @ApiOperation("操作人提交审批工单")
    @PostMapping("/completeOperatorTask")
    public void completeOperatorTask(
        @RequestBody @Validated FormParam form
    ) { 
        // 调用服务层方法，完成操作人的审批工单任务
        operatorService.completeOperatorTask(form.getTaskId(), form.getForm());
    }

    /**
     * 操作人查询已完成的受理任务
     * 该接口不接受任何参数，返回操作人已完成的任务列表。
     * 
     * @return 返回类型为List<TaskVo>，表示操作人已完成的任务列表。
     */
    @ApiOperation("操作人查询已完成的受理任务")
    @GetMapping("/listOperatorTasksCompleted")
    public List<TaskVo> listOperatorTasksCompleted() {
        // 获取当前操作人的ID
        Long operatorId = UserContext.getCurrentUserId();
        // 根据操作人ID查询操作人信息
        OperatorVo operatorVo = operatorService.selectByOperatorId(operatorId);
        List<TaskVo>taskVos= new ArrayList<>();
        
        // 遍历操作人所有的任务ID，查询每个任务的详细信息
        operatorVo.getTaskIds().forEach(taskId -> {
            try{
                // 尝试获取任务的详细信息并添加到任务列表中
                taskVos.add(operatorService.getHistoricalDetails(taskId));
            }catch(Exception e){
                // 如果查询异常，则跳过当前任务的处理
                return;
            }
        });
        
        return taskVos;
    }

    /**
     * 根据任务ID查询已完成的单个任务详情。
     * 
     * @param taskId 任务的唯一标识符，通过URL路径变量传递。
     * @return 返回任务的详细信息，封装在TaskVo对象中。
     */
    @ApiOperation("操作人查询已完成的单个任务")
    @GetMapping("/getTaskCompleted/{taskId}")
    public TaskVo getTaskCompleted(
        @PathVariable(value = "taskId") String taskId
    ) { 
        // 通过任务ID获取已完成任务的详细信息
        return operatorService.getHistoricalDetails(taskId);
    }

    /**
     * 操作人查询已提交的任务表单
     * 
     * @param taskId 任务的唯一标识符，通过URL路径变量传递。
     * @return 返回一个FormVo对象，包含指定任务的表单信息。
     */
    @ApiOperation("操作人查询已提交的任务表单")
    @GetMapping("/getHistoricalForm/{taskId}")
    public FormVo getHistoricalForm(
        @PathVariable(value = "taskId") String taskId
    ){ 
        // 通过任务ID查询历史表单信息
        FormVo formVo = applicantService.getHistoricalForm(taskId);
        return formVo;
    }

    /**
     * 操作人查询任务的流程进度
     * 
     * @param taskId 任务的唯一标识符，通过URL路径变量传递
     * @return 返回一个任务进度的视图列表（TaskVo），包含了指定任务的流程进度信息
     * @throws ApiException 如果指定的任务不存在，则抛出异常
     */
    @ApiOperation("操作人查询任务的流程进度")
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
        // 根据流程实例ID查询任务的进度信息
        List<TaskVo>taskVos=applicantService.getProcessInstanceProgress(processInstanceId);
        return taskVos;
    }
}
