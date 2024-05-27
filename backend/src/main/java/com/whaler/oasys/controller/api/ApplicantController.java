package com.whaler.oasys.controller.api;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.StartFormParam;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/applicant")
@Api(description = "申请人权限")
public class ApplicantController {
    @Autowired
    private ApplicantService applicantService;

    /**
     * 查询申请人发起的流程实例列表
     * <p>
     * 该接口不需要接受任何参数，通过当前登录用户的ID来查询其发起的流程实例信息。
     * 主要用于展示申请人自身发起的流程实例列表，以便于跟踪和管理。
     *
     * @return 返回一个流程实例Vo列表，包含申请人发起的所有流程实例的信息。
     */
    @GetMapping("/listProcessInstances")
    public List<ProcessInstanceVo> listProcessInstances(){
        // 获取当前用户ID
        Long applicantId=UserContext.getCurrentUserId();
        // 根据申请人ID查询其发起的流程实例
        List<ProcessInstanceVo> processInstanceVos = applicantService.listProcessInstances(applicantId);
        return processInstanceVos;
    }

    /**
     * 查询当前用户未完成的流程实例列表。
     * 该接口不接受任何参数，通过用户上下文获取当前用户ID，然后查询该用户所有未结束的流程实例。
     *
     * @return 返回一个未完成流程实例的列表，列表中每个元素都是一个ProcessInstanceVo对象。
     */
    @ApiOperation("申请人查询未结束的流程实例")
    @GetMapping("/listProcessInstancesNotCompleted")
    public List<ProcessInstanceVo> listProcessInstancesNotCompleted(){
        // 获取当前用户ID
        Long applicantId=UserContext.getCurrentUserId();
        // 查询当前用户所有的流程实例
        List<ProcessInstanceVo> processInstanceVos = applicantService.listProcessInstances(applicantId);
        
        // 过滤出未完成的流程实例
        List<ProcessInstanceVo> processInstanceVosNotCompleted = processInstanceVos.stream()
            .filter(processInstanceVo -> {
                return !processInstanceVo.getIsCompeleted();
            }).collect(Collectors.toList());
        
        return processInstanceVosNotCompleted;
    }

    /**
     * 查询当前用户已结束的流程实例列表。
     * <p>
     * 该接口不接受任何参数，通过用户上下文获取当前用户ID，然后查询该用户所有的流程实例，
     * 并从这些实例中筛选出已经结束的实例返回。
     *
     * @return 返回一个包含已结束流程实例的列表。
     */
    @ApiOperation("申请人查询已结束的流程实例")
    @GetMapping("/listProcessInstancesCompleted")
    public List<ProcessInstanceVo> listProcessInstancesCompleted(){
        // 获取当前用户ID
        Long applicantId=UserContext.getCurrentUserId();
        // 查询当前用户所有的流程实例
        List<ProcessInstanceVo> processInstanceVos = applicantService.listProcessInstances(applicantId);
        
        // 筛选出已结束的流程实例
        List<ProcessInstanceVo> processInstanceVosCompleted = processInstanceVos.stream()
            .filter(processInstanceVo -> {
                return processInstanceVo.getIsCompeleted();
            }).collect(Collectors.toList());
        
        return processInstanceVosCompleted;
    }

    /**
     * 查询申请人所有的流程定义信息
     * <p>
     * 该接口不需要申请人提供任何参数，仅通过申请人身份鉴权后，查询申请人所拥有的所有流程定义信息。
     * 主要用于申请人查看自己可以启动的流程定义列表。
     *
     * @return 返回一个包含所有流程定义信息的列表。每个流程定义信息都以ProcessDefinitionVo对象的形式呈现。
     */
    @ApiOperation("申请人查询所有的流程定义")
    @GetMapping("/listProcessDefinitions")
    public List<ProcessDefinitionVo> listProcessDefinitions(){
        // 调用申请人服务，获取流程定义列表
        return applicantService.listProcessDefinitions();
    }

    /**
     * 申请人创建流程实例
     * 
     * @param processDefinitionKey 流程定义的键值，用于指定要创建的流程实例的类型。
     *                              它是流程定义在Activiti引擎中的唯一标识。
     * @return ProcessInstanceVo 流程实例的详细信息，包括实例ID等。
     */
    @ApiOperation("申请人创建流程实例")
    @GetMapping("/createProcessInstance/{processDefinitionKey}")
    public ProcessInstanceVo createProcessInstance(
        @PathVariable(value = "processDefinitionKey") String processDefinitionKey
    ){
        // 调用申请人服务，根据流程定义键创建流程实例
        return applicantService.createProcessInstance(processDefinitionKey);
    }

    /**
     * 申请人查询提交的工单模板
     * 
     * @param processDefinitionKey 工单模板的流程定义键，用于查询对应的工单启动表单。
     * @return 返回工单启动表单的内容，通常为HTML表单字符串。
     */
    @ApiOperation("申请人查询提交的工单模板")
    @GetMapping("/getStartForm/{processDefinitionKey}")
    public String getStartForm(
        @PathVariable(value = "processDefinitionKey") String processDefinitionKey
    ){
        // 通过流程定义键查询并返回工单的启动表单
        return applicantService.getStartForm(processDefinitionKey);
    }

    /**
     * 提交启动表单的申请。
     * 该接口用于申请人提交已经填写完成的工单表单。
     * 
     * @param formParam 包含工单流程实例ID和表单数据的参数对象，必须是有效的。
     *                  <p>注：该参数通过RequestBody接收，确保了请求体中包含必要的工单启动信息。</p>
     * @apiNote 该方法不返回任何内容，即void类型。操作结果通过服务层方法申请人提交表单后，进一步处理。
     */
    @PostMapping("/submitStartForm")
    public void submitStartForm(
        @RequestBody @Validated StartFormParam formParam
    ){
        // 将表单参数中的流程实例ID和表单数据提交给申请人服务进行处理
        applicantService.submitStartForm(formParam.getProcessInstanceId(), formParam.getForm());
    }

    /**
     * 查询指定流程实例的进度
     * 
     * @param processInstanceId 流程实例的ID，通过路径变量传递
     * @return 返回一个任务视图列表，列表中的每个任务视图代表流程中的一个任务节点及其进度信息
     */
    @ApiOperation("申请人查询流程实例的进度")
    @GetMapping("/getProcessInstanceProgress/{processInstanceId}")
    public List<TaskVo> getProcessInstanceProgress(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){
        // 调用申请人服务，获取指定流程实例的进度信息
        return applicantService.getProcessInstanceProgress(processInstanceId);
    }

    /**
     * 查询流程任务节点的详细情况。
     * 该接口用于申请人获取指定任务节点的历史表单信息。
     *
     * @param taskId 任务节点的ID，通过路径变量传递。
     * @return 返回任务节点的历史表单信息，封装在FormVo对象中。
     */
    @ApiOperation("申请人查询流程任务节点的详细情况")
    @GetMapping("/getHistoricalForm/{taskId}")
    public FormVo getHistoricalForm(
        @PathVariable(value = "taskId") String taskId
    ){
        // 调用申请人服务，获取指定任务节点的历史表单
        return applicantService.getHistoricalForm(taskId);
    }

    /**
     * 查询指定流程实例的详细信息。
     * 
     * @param processInstanceId 流程实例的ID，通过路径变量传递。
     * @return 返回流程实例的详细信息对象（ProcessInstanceVo）。
     */
    @ApiOperation("申请人查询流程实例的详细情况")
    @GetMapping("/getProcessInstance/{processInstanceId}")
    public ProcessInstanceVo getProcessInstance(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){ 
        // 通过申请人服务获取指定流程实例的详细信息
        return applicantService.getProcessInstance(processInstanceId);
    }

    /**
     * 终止流程实例。
     * 申请人可以通过该接口终止一个正在运行的流程实例，并提供终止原因。
     *
     * @param processInstanceId 流程实例的ID，用于标识需要终止的流程实例。
     * @param reason 终止流程的原因，便于记录和后续查询。
     */
    @PostMapping("/abortProcessInstance")
    public void abortProcessInstance(
        @RequestParam(value = "processInstanceId") String processInstanceId,
        @RequestParam(value = "reason") String reason
    ){ 
        // 调用申请人服务，终止指定的流程实例
        applicantService.abortProcessInstance(processInstanceId, reason);
    }

    /**
     * 申请人查询流程实例的原始流程图。
     * 
     * @param processDefinitionKey 流程定义的键，用于指定要查询的流程图。
     * @return 返回流程图的字节数组，以供客户端直接展示或处理。
     * @throws ApiException 如果获取流程图过程中发生异常，则抛出此异常。
     */
    @ApiOperation("申请人查询流程实例的原始流程图")
    @GetMapping(value = "/getOriginalProcessDiagram/{processDefinitionKey}",
        produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getOriginalProcessDiagram(
        @PathVariable(value = "processDefinitionKey") String processDefinitionKey
    ){
        // 从申请人服务中根据流程定义键获取原始流程图的输入流
        InputStream inputStream = applicantService.getOriginalProcessDiagram(processDefinitionKey);
        byte[] bytes;
        try {
            // 将输入流中的图片数据读取到字节数组中
            bytes=new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            inputStream.close();
        } catch (Exception e) {
            // 若在读取流程图过程中发生异常，则抛出API异常
            throw new ApiException("获取流程图失败");
        }
        return bytes;
    }

    /**
     * 申请人查询流程实例的流程图
     * 
     * @param processInstanceId 流程实例的ID，用于查询对应的流程图。
     * @return 返回流程图的字节数组，以供前端展示。
     */
    @ApiOperation("申请人查询流程实例的流程图")
    @GetMapping(value = "/getProcessInstanceDiagram/{processInstanceId}",
        produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getProcessInstanceDiagram(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){
        // 从申请人服务中获取指定流程实例的流程图输入流
        InputStream inputStream = applicantService.getProcessInstanceDiagram(processInstanceId);
        byte[] bytes;
        try {
            // 将输入流中的图片数据读取为字节数组
            bytes=new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            inputStream.close();
        } catch (Exception e) {
            // 若读取流程图失败，则抛出异常
            throw new ApiException("获取流程图失败");
        }
        return bytes;
    }
}
