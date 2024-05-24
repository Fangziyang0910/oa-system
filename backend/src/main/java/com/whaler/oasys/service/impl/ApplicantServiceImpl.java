package com.whaler.oasys.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Event;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormInfo;
import org.flowable.form.model.SimpleFormModel;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.ApplicantMapper;
import com.whaler.oasys.model.entity.ApplicantEntity;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.vo.ApplicantVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.FormFieldVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;
import com.whaler.oasys.service.UserService;
import com.whaler.oasys.tool.MyDefaultProcessDiagramGenerator;

@Service
public class ApplicantServiceImpl
extends ServiceImpl<ApplicantMapper,ApplicantEntity>
implements ApplicantService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private FormService formService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private MyDefaultProcessDiagramGenerator myDefaultProcessDiagramGenerator;
    @Autowired
    private UserService userService;

    /**
     * 插入申请人实体到数据库。
     * 
     * @param applicantId 申请人的唯一标识符。
     * @param processinstanceId 申请关联的流程实例ID。
     * @return 返回插入操作影响的行数。
     */
    @Override
    public int insertApplicantEntity(Long applicantId, String processinstanceId) {
        // 调用baseMapper的insertApplicantEntity方法插入申请人实体
        return this.baseMapper.insertApplicantEntity(applicantId, processinstanceId);
    }

    /**
     * 删除申请人实体
     * 
     * @param applicantId 申请人的ID，用于确定要删除的申请人
     * @param processinstanceId 流程实例的ID，用于确定申请人参与的具体流程实例
     * @return 返回删除操作影响的行数，通常为1表示删除成功，为0表示删除失败或未找到相应申请人实体
     */
    @Override
    public int deleteApplicantEntity(Long applicantId, String processinstanceId) {
        // 调用baseMapper删除指定ID的申请人实体
        return this.baseMapper.deleteApplicantEntity(applicantId, processinstanceId);
    }

    /**
     * 根据申请人ID选择对应的申请人信息。
     * 
     * @param applicantId 申请人的唯一标识ID。
     * @return 返回一个申请人视图对象（ApplicantVo），包含了申请人的详细信息和相关流程实例ID列表。
     */
    @Override
    public ApplicantVo selectByApplicantId(Long applicantId) {
        // 通过申请人ID查询申请人实体列表
        List<ApplicantEntity> applicantEntities = this.baseMapper.selectByApplicantId(applicantId);
        ApplicantVo applicantVo = new ApplicantVo();
        applicantVo.setApplicantId(applicantId);
        
        // 将查询到的申请人实体列表中的流程实例ID映射并收集到一个列表中，然后设置到申请人视图对象中
        applicantVo.setProcessinstanceIds(
            applicantEntities.stream().map(ApplicantEntity::getProcessinstanceId)
            .collect(Collectors.toList())
        );
        return applicantVo;
    }

    /**
     * 列出指定申请人的所有流程实例。
     * 
     * @param applicantId 申请人ID，用于查询与此ID对应的申请人信息。
     * @return 返回一个包含申请人所有流程实例的列表。如果某个流程实例获取失败，则不会包含在返回列表中。
     */
    @Override
    public List<ProcessInstanceVo> listProcessInstances(Long applicantId) {
        // 根据申请人ID查询申请人信息
        ApplicantVo applicantVo = this.selectByApplicantId(applicantId);
        List<ProcessInstanceVo> processInstanceVos = new ArrayList<>();
        
        // 遍历并尝试获取每个申请人的流程实例
        applicantVo.getProcessinstanceIds().forEach(
            processInstanceId -> {
                try{
                    // 尝试获取流程实例，并添加到结果列表中
                    processInstanceVos.add(this.getProcessInstance(processInstanceId));
                }catch(Exception e){
                    // 如果获取流程实例发生异常，则忽略此流程实例，不添加到结果列表中
                    // 为了保持代码简洁，此处不记录异常日志或进行其他处理
                }
            }
        );
        return processInstanceVos;
    }

    /**
     * 列出所有的流程定义信息。
     * 
     * @return 返回包含多个流程定义信息的列表。每个流程定义信息包括：流程定义ID、流程定义键、流程定义名称、
     *         流程定义分类、流程定义描述和流程定义版本。
     */
    @Override
    public List<ProcessDefinitionVo> listProcessDefinitions() {
        // 从流程定义查询中获取所有流程定义，并将每个流程定义转换为ProcessDefinitionVo对象
        List<ProcessDefinitionVo>processDefinitionVoList= 
            repositoryService.createProcessDefinitionQuery().list()
            .stream()
            .map(processDefinition -> new ProcessDefinitionVo()
                .setProcessDefinitionId(processDefinition.getId())
                .setProcessDefinitionKey(processDefinition.getKey())
                .setProcessDefinitionName(processDefinition.getName())
                .setProcessDefinitionCategory(processDefinition.getCategory())
                .setProcessDefinitionDescription(processDefinition.getDescription())
                .setProcessDefinitionVersion(processDefinition.getVersion())
            ).collect(Collectors.toList());

        return processDefinitionVoList;
    }

    /**
     * 创建流程实例。
     * 
     * @param processDefinitionKey 流程定义的键。此键用于查询流程定义。
     * @return ProcessInstanceVo 流程实例的视图对象，包含了流程实例和其相关流程定义的详细信息。
     * @throws ApiException 如果流程定义不存在或流程实例创建失败，则抛出此异常。
     */
    @Override
    public ProcessInstanceVo createProcessInstance(String processDefinitionKey) {
        // 获取当前用户ID，作为流程启动者的ID
        String starterId = Long.toString(UserContext.getCurrentUserId());
        // 根据流程定义键查询流程定义信息
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).singleResult();
        // 如果查询到的流程定义为空，则抛出异常
        if(pd==null){
            throw new ApiException("流程定义不存在");
        }
        // 初始化变量集合，用于存储流程启动时的变量
        Map<String,Object>vars=new HashMap<>();
        vars.put("starter", starterId);
        // 初始化表单列表和服务列表，并将其序列化为JSON字符串，存储在变量中
        List<String> formList=new ArrayList<>();
        vars.put("formList", JSON.toJSONString(formList));
        List<String> serviceList=new ArrayList<>();
        vars.put("serviceList", JSON.toJSONString(serviceList));
        // 根据流程定义键和变量集合，启动流程实例
        ProcessInstance pi=runtimeService.startProcessInstanceByKey(processDefinitionKey, vars);
        // 如果创建的流程实例为空，则抛出异常
        if (pi==null) {
            throw new ApiException("流程实例创建失败");
        }
        // 插入申请人实体信息，关联用户ID和流程实例ID
        insertApplicantEntity(UserContext.getCurrentUserId(), pi.getProcessInstanceId());
        // 创建并配置流程实例视图对象，准备返回
        ProcessInstanceVo processInstanceVo=new ProcessInstanceVo();
        processInstanceVo.setProcessInstanceId(pi.getProcessInstanceId())
            .setProcessDefinition(
                new ProcessDefinitionVo()
                    .setProcessDefinitionId(pd.getId())
                    .setProcessDefinitionKey(pd.getKey())
                    .setProcessDefinitionName(pd.getName())
                    .setProcessDefinitionCategory(pd.getCategory())
                    .setProcessDefinitionDescription(pd.getDescription())
                    .setProcessDefinitionVersion(pd.getVersion())
            );
        return processInstanceVo;
    }

    /**
     * 根据流程定义键获取启动表单。
     * 
     * @param processDefinitionKey 流程定义的键。
     * @return 表单内容的JSON字符串。
     * @throws ApiException 如果流程定义不存在或表单key不存在，抛出此异常。
     */
    @Override
    public String getStartForm(String processDefinitionKey) {
        // 根据流程定义键查询流程定义
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).singleResult();
        if (pd==null) {
            throw new ApiException("流程定义不存在");
        }

        // 获取Bpmn模型
        BpmnModel bpmnModel=repositoryService.getBpmnModel(pd.getId());
        // 获取启动事件，并提取表单key
        StartEvent startEvent=(StartEvent)bpmnModel.getMainProcess().getFlowElement("startEvent");
        String formKey=startEvent.getFormKey();
        if (formKey==null) {
            throw new ApiException("表单key不存在");
        }

        // 构建表单资源路径
        String path="/forms/"+formKey;
        ClassPathResource classPathResource = new ClassPathResource(path);
        InputStream inputStream=null;
        String startForm=null;
        try{
            // 尝试从资源路径加载表单内容
            inputStream= classPathResource.getInputStream();
            startForm = IOUtils.toString(inputStream, "UTF-8");
            // 将表单内容转换为JSON字符串
            JSONObject jsonobject = JSON.parseObject(startForm);
            startForm = jsonobject.toJSONString();
        }catch(Exception e){
            throw new ApiException("表单文件不存在");
        }
        return startForm;
    }

    /**
     * 提交开始表单。
     * 该方法用于在流程实例启动时提交相关的表单数据，并更新流程的进度。
     * 
     * @param processInstanceId 流程实例的ID。
     * @param startForm 启动表单的数据映射。
     * @throws ApiException 如果尝试提交的表单已经提交过，则抛出此异常。
     */
    @Override
    public void submitStartForm(String processInstanceId, Map<String, String> startForm) {
        // 获取当前用户的ID，将其转换为字符串
        String starterId = Long.toString(UserContext.getCurrentUserId());
        
        // 查询当前用户是否已经有对应的启动任务
        Task starterTask=taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .taskAssignee(starterId).singleResult();
        
        // 如果已经有任务存在，则表示表单已提交，抛出异常
        if(starterTask==null){
            throw new ApiException("已提交申请表");
        }
        
        // 更新流程进度，记录表单提交信息
        String jsonString=(String)runtimeService.getVariable(starterTask.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(starterTask.getId());
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(starterTask.getExecutionId(), "formList", jsonString);
        
        // 设置当前任务的提交人姓名
        String userName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();
        runtimeService.setVariable(starterTask.getExecutionId(), starterTask.getId(), userName);

        // 提交申请表单
        formService.submitTaskFormData(starterTask.getId(), startForm);
        
        // 在流程实例中设置开始表单的任务ID
        runtimeService.setVariable(processInstanceId, "startFormTask", starterTask.getId());
    }

    /**
     * 获取指定流程实例的进度信息。
     * 
     * @param processInstanceId 流程实例的ID。
     * @return 返回一个包含流程任务进度信息的列表。
     * @throws ApiException 如果流程实例不存在或formList不存在，则抛出此异常。
     */
    @Override
    public List<TaskVo> getProcessInstanceProgress(String processInstanceId){
        // 查询指定ID的流程实例历史信息
        HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
        if(pi==null){
            throw new ApiException("流程不存在");
        }

        // 获取并解析formList变量，包含流程中所有任务ID
        String jsonString=(String)historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(processInstanceId)
            .variableName("formList")
            .singleResult().getValue();
        List<String>formList=JSON.parseArray(jsonString, String.class);
        if (formList==null) {
            throw new ApiException("formList 不存在");
        }

        // 初始化任务进度信息列表
        List<TaskVo>taskVos=new ArrayList<>();
        formList.forEach(
            taskId->{
                try{
                    // 查询指定任务ID的历史任务实例
                    HistoricTaskInstance task=historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                    if(task==null){
                        // 如果任务不存在，则添加一个含有null ID的特殊任务项
                        taskVos.add(new TaskVo().setTaskId("null")
                            .setTaskName(taskId)
                            .setDescription(pi.getDeleteReason())
                            .setEndTime(pi.getEndTime().toString())
                        );
                        return;
                    }
                    // 获取任务相关变量（如处理人等）并构造任务进度信息对象
                    String userName=(String)historyService.createHistoricVariableInstanceQuery()
                        .processInstanceId(task.getProcessInstanceId()).variableName(taskId)
                        .singleResult().getValue();
                    TaskVo taskVo = new TaskVo().setTaskId(task.getId())
                        .setTaskName(task.getName())
                        .setExecutionId(task.getExecutionId())
                        .setAssigneeName(userName)
                        .setDescription(task.getDescription())
                        .setEndTime(task.getEndTime().toString());
                    if (task.getDueDate() != null) {
                        // 如果任务有截止日期，则设置截止时间信息
                        taskVo.setDueTime(task.getDueDate().toString());
                    }
                    taskVos.add(taskVo);
                }catch(Exception e){
                    // 异常情况下直接忽略当前任务的处理
                    return;
                }
            }
        );
        return taskVos;
    }

    /**
     * 获取指定任务的历史表单信息。
     * 
     * @param taskId 任务的ID，用于查询历史任务实例。
     * @return FormVo 表单信息的视图对象，包含表单的键、名称和字段信息。
     * @throws ApiException 如果指定的任务ID不存在，则抛出此异常。
     */
    @Override
    public FormVo getHistoricalForm(String taskId) {
        // 查询指定ID的历史任务实例
        HistoricTaskInstance task=historyService.createHistoricTaskInstanceQuery()
            .taskId(taskId)
            .singleResult();
        // 如果任务不存在，则抛出异常
        if (task==null) {
            throw new ApiException("历史任务不存在");
        }
        // 获取任务关联的表单模型
        FormInfo formInfo=taskService.getTaskFormModel(taskId);
        // 将表单模型转换为SimpleFormModel类型
        SimpleFormModel simpleFormModel=(SimpleFormModel)formInfo.getFormModel();
        // 创建表单视图对象
        FormVo formVo=new FormVo();
        // 设置表单的键和名称
        formVo.setFormKey(formInfo.getKey());
        formVo.setFormName(formInfo.getName());
        // 将表单字段信息转换为FormFieldVo对象列表，并设置到表单视图对象中
        formVo.setFormFields(simpleFormModel.getFields().stream().map(field->{
            FormFieldVo formFieldVo=new FormFieldVo();
            formFieldVo.setId(field.getId());
            formFieldVo.setName(field.getName());
            formFieldVo.setType(field.getType());
            formFieldVo.setValue(field.getValue());
            return formFieldVo;
        }).collect(Collectors.toList()));
        return formVo;
    }

    /**
     * 根据流程实例ID获取流程实例的详细信息。
     * 
     * @param processInstanceId 流程实例的ID。
     * @return ProcessInstanceVo 流程实例的详细信息对象，包括流程实例的基本信息、定义信息及进度。
     * @throws ApiException 如果指定的流程实例不存在，则抛出此异常。
     */
    @Override
    public ProcessInstanceVo getProcessInstance(String processInstanceId) {
        // 通过历史服务查询指定ID的流程实例
        HistoricProcessInstance pi=historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        // 如果查询结果为空，表示流程实例不存在，抛出异常
        if (pi==null) {
            throw new ApiException("流程实例不存在");
        }
        // 根据流程实例ID查询对应的流程定义信息
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(pi.getProcessDefinitionId()).singleResult();
        // 获取当前流程实例的进度信息
        List<TaskVo> progress=getProcessInstanceProgress(processInstanceId);
        // 初始化流程实例VO对象，用于返回
        ProcessInstanceVo processInstanceVo=new ProcessInstanceVo();
        // 设置流程实例的基本信息
        processInstanceVo.setProcessInstanceId(pi.getId())
            .setProcessDefinition(
                new ProcessDefinitionVo()
                    .setProcessDefinitionId(pd.getId())
                    .setProcessDefinitionKey(pd.getKey())
                    .setProcessDefinitionName(pd.getName())
                    .setProcessDefinitionCategory(pd.getCategory())
                    .setProcessDefinitionDescription(pd.getDescription())
                    .setProcessDefinitionVersion(pd.getVersion())
            )
            .setStartTime(pi.getStartTime().toString())
            .setProgress(progress);
        // 判断流程实例是否完成，未完成则设置相关标志和结束时间
        if (pi.getEndTime()==null) {
            processInstanceVo.setIsCompeleted(false);
        }else{
            // 已完成的流程实例设置结束时间和终止原因
            processInstanceVo.setEndTime(pi.getEndTime().toString());
            processInstanceVo.setAbortReason(pi.getDeleteReason());
            processInstanceVo.setIsCompeleted(true);
        }
        return processInstanceVo;
    }

    /**
     * 终止一个正在运行的流程实例。
     * 
     * @param processInstanceId 要终止的流程实例ID。
     * @param reason 终止流程的 reasons。
     * @throws ApiException 如果流程实例不存在、已经结束，或流程执行实例已经结束，则抛出异常。
     */
    @Override
    public void abortProcessInstance(String processInstanceId, String reason) {
        // 查询历史流程实例信息
        HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        // 检查流程实例是否存在
        if (pi==null) {
            throw new ApiException("流程实例不存在");
        }
        // 检查流程实例是否已经结束
        if(pi.getEndTime()!=null){
            throw new ApiException("流程实例已经结束");
        }
        
        // 查找对应的执行实例，记录流程终止原因
        Execution execution =  runtimeService.createExecutionQuery()
            .processInstanceId(pi.getId()).list().get(0);
        // 检查执行实例是否存在
        if (execution==null) {
            throw new ApiException("流程执行实例已经结束");
        }
        // 获取并更新表单数据列表，添加流程终止信息
        String jsonString=(String)runtimeService.getVariable(execution.getId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add("流程终止："+reason);
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(execution.getId(), "formList", jsonString);
        
        // 删除流程实例
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }

    /**
     * 根据流程定义键获取原始流程图的输入流。
     * 
     * @param processDefinitionKey 流程定义的键，用于查询特定的流程定义。
     * @return 返回表示流程图的输入流。
     * @throws ApiException 如果流程定义不存在或流程图生成失败，则抛出此异常。
     */
    @Override
    public InputStream getOriginalProcessDiagram(String processDefinitionKey) {
        // 根据流程定义键查询流程定义
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(processDefinitionKey)
            .singleResult();
        if (processDefinition==null) {
            // 如果流程定义不存在，抛出异常
            throw new ApiException("流程定义不存在");
        }
        
        // 根据流程定义的ID获取BPMN模型
        BpmnModel bpmnModel=repositoryService.getBpmnModel(processDefinition.getId());

        // 定义流程图的图像类型和字体设置
        String IMAGE_TYPE = "png";
        String activityFontName = "宋体";
        String annotationFontName = "宋体";
        String labelFontName = "宋体";
        
        // 使用默认流程图生成器生成流程图
        InputStream diagram=myDefaultProcessDiagramGenerator.generateDiagram(
            bpmnModel, IMAGE_TYPE, activityFontName, labelFontName, annotationFontName,
        null, 1.0d, false);
        if (diagram==null) {
            // 如果流程图生成失败，抛出异常
            throw new ApiException("流程图生成失败");
        }
        return diagram;
    }

    /**
     * 获取流程实例的图片表示。
     * 
     * @param processInstanceId 流程实例的ID。
     * @return 返回表示流程实例的图片的InputStream。
     * @throws ApiException 如果流程实例不存在或流程图生成失败时抛出。
     */
    @Override
    public InputStream getProcessInstanceDiagram(String processInstanceId) {
        // 查询历史流程实例
        HistoricProcessInstance historicProcessInstance=historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        if (historicProcessInstance==null) {
            throw new ApiException("流程实例不存在");
        }
        // 根据流程实例ID查询流程定义
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(
            historicProcessInstance.getProcessDefinitionKey()
        ).singleResult();
        
        // 获取流程模型
        BpmnModel bpmnModel=repositoryService.getBpmnModel(processDefinition.getId());

        // 获取流程实例的进度信息
        List<TaskVo>tasks=getProcessInstanceProgress(processInstanceId);
        // 查询并解析服务列表变量
        String jsonString=(String)historyService.createHistoricVariableInstanceQuery()
        .processInstanceId(processInstanceId)
        .variableName("serviceList").singleResult().getValue();
        List<String>serviceList=JSON.parseArray(jsonString, String.class);
        
        // 初始化高亮活动和流程列表
        List<String>highLightedActivities=new ArrayList<>();
        Set<String>activitySet=new HashSet<>();
        // 遍历任务，将当前任务和服务列表中的活动加入高亮活动列表
        for (TaskVo task : tasks) {
            HistoricTaskInstance taskInstance=historyService.createHistoricTaskInstanceQuery()
                .taskId(task.getTaskId()).singleResult();
            if (taskInstance==null) {
                continue;
            }
            highLightedActivities.add(taskInstance.getTaskDefinitionKey());
            activitySet.add(taskInstance.getTaskDefinitionKey());
        }
        for (String service : serviceList) {
            highLightedActivities.add(service);
            activitySet.add(service);
        }
        // 初始化高亮流程列表
        List<String>highLightedFlows=new ArrayList<>();
        // 根据高亮活动列表，收集高亮流程列表
        for (String highLightedActivity : highLightedActivities) {
            FlowNode flowNode= (FlowNode)bpmnModel.getFlowElement(highLightedActivity);
            List<SequenceFlow>incomingFlows=flowNode.getIncomingFlows();
            for (SequenceFlow sequenceFlow : incomingFlows) {
                String sourceRef=sequenceFlow.getSourceRef();
                // 如果源节点为事件或网关，则加入高亮流程列表；否则，如果源节点在活动集合中，则加入高亮流程列表
                if(bpmnModel.getFlowElement(sourceRef)
                    .getClass().getSuperclass().equals(Event.class)
                    || bpmnModel.getFlowElement(sourceRef)
                    .getClass().getSuperclass().equals(Gateway.class)){
                    highLightedFlows.add(sequenceFlow.getId());
                }
                else if (activitySet.contains(sourceRef)) {
                    highLightedFlows.add(sequenceFlow.getId());
                }
            }
            List<SequenceFlow>outgoingFlows=flowNode.getOutgoingFlows();
            for (SequenceFlow sequenceFlow : outgoingFlows) {
                String targetRef=sequenceFlow.getTargetRef();
                // 如果目标节点为事件或网关，则加入高亮流程列表
                if(bpmnModel.getFlowElement(targetRef)
                    .getClass().getSuperclass().equals(Event.class)
                    || bpmnModel.getFlowElement(targetRef)
                    .getClass().getSuperclass().equals(Gateway.class)){
                        highLightedFlows.add(sequenceFlow.getId());
                    }
            }
        }

        // 生成流程图
        String IMAGE_TYPE = "png";
        String activityFontName = "宋体";
        String annotationFontName = "宋体";
        String labelFontName = "宋体";
        InputStream diagram=myDefaultProcessDiagramGenerator.generateDiagram(bpmnModel, IMAGE_TYPE, highLightedActivities,
        highLightedFlows, activityFontName, labelFontName, annotationFontName,
        null, 1.0d, false);

        if (diagram==null) {
            throw new ApiException("流程图生成失败");
        }
        return diagram;
    }

}
