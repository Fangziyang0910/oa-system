package com.whaler.oasys.service.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.form.api.FormInfo;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.ApproverMapper;
import com.whaler.oasys.model.entity.ApproverEntity;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.vo.ApproverVo;
import com.whaler.oasys.model.vo.FormFieldVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApproverService;
import com.whaler.oasys.service.CategoryService;
import com.whaler.oasys.service.OperatorService;
import com.whaler.oasys.service.UserService;

@Service
public class ApproverServiceImpl
extends ServiceImpl<ApproverMapper,ApproverEntity>
implements ApproverService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private FormService formService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OperatorService operatorService;

    /**
     * 插入审批人实体到数据库。
     * 
     * @param approverId 审批人的ID，用于标识唯一的审批人。
     * @param processinstanceId 流程实例的ID，标识唯一的流程实例。
     * @return 返回插入操作的影响行数，通常为1表示插入成功。
     */
    @Override
    public int insertApproverEntity(Long approverId, String processinstanceId) {
        // 调用baseMapper的insertApproverEntity方法插入审批人实体
        return this.baseMapper.insertApproverEntity(approverId, processinstanceId);
    }

    /**
     * 删除审批实体
     * 
     * @param approverId 审批者ID，用于指定要删除的审批者的实体。
     * @param processinstanceId 流程实例ID，用于指定删除操作所在的流程实例。
     * @return 返回删除操作影响的行数，通常为1表示删除成功，为0表示未找到对应的审批实体。
     */
    @Override
    public int deleteApproverEntity(Long approverId, String processinstanceId) {
        // 调用baseMapper删除指定的审批实体
        return this.baseMapper.deleteApproverEntity(approverId, processinstanceId);
    }

    /**
     * 根据审批人ID选择对应的审批人信息。
     * 
     * @param approverId 审批人的ID，用于查询审批人及其相关的审批任务。
     * @return 返回一个审批人视图对象（ApproverVo），包含了审批人的ID和相关的任务ID列表。
     */
    @Override
    public ApproverVo selectByApproverId(Long approverId) {
        // 根据审批人ID查询审批人实体列表
        List<ApproverEntity> approverEntities = this.baseMapper.selectByApproverId(approverId);
        ApproverVo approverVo = new ApproverVo();
        approverVo.setApproverId(approverId);
        
        // 将审批实体的流程实例ID映射为审批视图对象的任务ID列表
        approverVo.setTaskIds(
            approverEntities.stream()
            .map(ApproverEntity::getProcessinstanceId)
            .collect(Collectors.toList())
        );
        return approverVo;
    }

    /**
     * 列出当前用户的审批任务。
     * 该方法不接受任何参数，返回当前用户需要审批的任务列表。
     * 
     * @return 返回一个包含多个TaskVo对象的列表，每个对象代表一个审批任务。
     */
    @Override
    public List<TaskVo> listApprovalTasks() {
        // 获取当前用户ID
        Long userId=UserContext.getCurrentUserId();
        // 根据用户ID获取用户名
        String userName=userService.selectByUserId(userId).getName();
        // 获取当前用户的权限ID
        Long permissionId=userService.getById(UserContext.getCurrentUserId()).getPermissionId();
        // 根据权限ID获取分类ID列表，并转换为字符串列表
        List<String> categoryIds=categoryService.selectCategoryIdsByPermissionId(permissionId)
            .stream().map(categoryId->Long.toString(categoryId)).collect(Collectors.toList());

        // 查询当前用户待处理的审批任务
        List<Task>approvalTasks=taskService.createTaskQuery().or()
            .taskAssignee(userName)
            .taskCandidateUser(Long.toString(permissionId))
            .taskCandidateGroupIn(categoryIds).endOr()
            .taskDescription("审批")
            .list();

        // 将查询到的任务转换为TaskVo对象列表
        List<TaskVo>taskVos=approvalTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                // 获取任务发起人的姓名
                String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
                String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
                taskVo.setStarterName(starter);
                // 获取任务所属流程的名称
                String processDefinitionName=repositoryService.createProcessDefinitionQuery().processDefinitionId(
                    task.getProcessDefinitionId()
                ).singleResult().getName();
                taskVo.setProcessDefinitionName(processDefinitionName);
                taskVo.setDescription(task.getDescription());
                // 如果任务有截止日期，则设置其截止时间
                if (task.getDueDate() != null) {
                    taskVo.setDueTime(task.getDueDate().toString());   
                }
                return taskVo;
            }).collect(Collectors.toList());
        return taskVos;
    }

    /**
     * 列出当前用户的审批候选任务列表。
     * 该方法首先根据当前用户ID获取其权限ID，然后根据权限ID查询相关的分类ID列表。
     * 使用这些信息，查询当前用户作为候选用户或候选组成员的任务，且任务描述为“审批”。
     * 对查询到的任务进行加工，转换为TaskVo对象后返回。
     *
     * @return List<TaskVo> 返回加工后的任务视图对象列表，每个对象包含任务的各种信息，如任务ID、任务名等。
     */
    @Override
    public List<TaskVo> listApprovalCandidateTasks() {
        // 获取当前用户的权限ID
        Long permissionId=userService.getById(UserContext.getCurrentUserId()).getPermissionId();
        
        // 根据权限ID查询相关的分类ID列表，并转换为字符串列表
        List<String> categoryIds=categoryService.selectCategoryIdsByPermissionId(permissionId)
            .stream().map(categoryId->Long.toString(categoryId)).collect(Collectors.toList());

        // 查询当前用户作为候选用户或候选组成员，且任务描述为“审批”的任务列表
        List<Task>approvalTasks=taskService.createTaskQuery().or()
            .taskCandidateUser(Long.toString(permissionId))
            .taskCandidateGroupIn(categoryIds).endOr()
            .taskDescription("审批")
            .list();

        // 对查询到的任务进行加工，转换为TaskVo对象列表
        List<TaskVo>taskVos=approvalTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                
                // 获取任务发起人的名称
                String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
                String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
                taskVo.setStarterName(starter);
                
                // 获取任务所属流程定义的名称
                String processDefinitionName=repositoryService.createProcessDefinitionQuery().processDefinitionId(
                    task.getProcessDefinitionId()
                ).singleResult().getName();
                taskVo.setProcessDefinitionName(processDefinitionName);
                taskVo.setDescription(task.getDescription());
                
                // 如果任务有截止日期，则设置其截止时间
                if (task.getDueDate() != null) {
                    taskVo.setDueTime(task.getDueDate().toString());   
                }
                return taskVo;
            }).collect(Collectors.toList());
        return taskVos;
    }

    /**
     * 申领候选任务。
     * 该方法尝试为当前用户申领指定的任务。如果任务申领成功，则无返回值。如果任务已经被申领，则抛出ApiException。
     * 
     * @param taskId 任务的唯一标识符，用于指定要申领的任务。
     * @throws ApiException 如果任务已经被申领，则抛出此异常。
     */
    @Override
    public void claimCandidateTask(String taskId) {
        // 获取当前用户的名字
        String userName=userService.getById(UserContext.getCurrentUserId()).getName();
        try{
            // 尝试申领任务
            taskService.claim(taskId, userName);
        }catch(Exception e){
            // 如果任务申领失败（即任务已被申领），则抛出异常
            throw new ApiException("任务已经被申领");
        }
    }

    /**
     * 取消认领候选任务。
     * 该方法用于取消一个之前被认领的候选任务，使其重新变为可被认领的状态。
     * 
     * @param taskId 任务的唯一标识符，用于指定要取消认领的任务。
     * 说明：该方法不返回任何结果，即没有返回值。
     */
    @Override
    public void unclaimCandidateTask(String taskId){
        operatorService.unclaimCandidateTask(taskId); // 调用operatorService的unclaimCandidateTask方法，取消指定taskId的候选任务认领
    }

    /**
     * 获取指定任务ID的未完成任务。
     * 
     * @param taskId 任务的唯一标识符。
     * @return 返回一个任务视图对象（TaskVo），表示指定ID的未完成任务。
     */
    @Override
    public TaskVo getTaskNotCompleted(String taskId){
        // 通过operatorService获取指定任务ID的未完成任务
        return operatorService.getTaskNotCompleted(taskId);
    }

    /**
     * 列出当前用户待审批的任务列表。
     * 该方法不接受任何参数，返回当前用户待审批的任务列表。
     *
     * @return 返回一个包含多个TaskVo对象的列表，每个对象代表一个待审批任务。
     */
    @Override
    public List<TaskVo> listApprovalAssignTasks() {
        // 获取当前用户的姓名
        String userName=userService.getById(UserContext.getCurrentUserId()).getName();

        // 查询当前用户待审批的任务
        List<Task>approvalTasks=taskService.createTaskQuery()
            .taskAssignee(userName)
            .taskDescription("审批")
            .list();

        // 将查询到的任务转换为TaskVo对象列表
        List<TaskVo>taskVos=approvalTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                
                // 获取任务发起人的姓名
                String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
                String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
                taskVo.setStarterName(starter);
                
                // 获取任务所属流程定义的名称
                String processDefinitionName=repositoryService.createProcessDefinitionQuery().processDefinitionId(
                    task.getProcessDefinitionId()
                ).singleResult().getName();
                taskVo.setProcessDefinitionName(processDefinitionName);
                taskVo.setDescription(task.getDescription());
                
                // 如果任务有截止日期，则设置任务的截止时间
                if (task.getDueDate() != null) {
                    taskVo.setDueTime(task.getDueDate().toString());   
                }
                return taskVo;
            }).collect(Collectors.toList());
        return taskVos;
    }

    /**
     * 获取开始表单的信息。
     * 
     * @param taskId 任务的ID，用于查询任务及其相关的开始表单。
     * @return FormVo 表示表单信息的视图对象，包括表单键、表单名称和表单字段。
     * @throws ApiException 如果任务不存在或表单不存在，则抛出此异常。
     */
    @Override
    public FormVo getStartForm(String taskId) {
        // 查询指定ID的任务
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        // 如果任务不存在，则抛出异常
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        
        // 从任务执行过程中获取开始表单的ID
        String startFormTaskId=runtimeService.getVariable(task.getExecutionId(), "startFormTask").toString();
        
        // 根据表单任务ID获取开始表单的信息
        FormInfo startFormInfo = taskService.getTaskFormModel(startFormTaskId);
        // 如果表单不存在，则抛出异常
        if(startFormInfo==null){
            throw new ApiException("表单不存在");
        }
        
        // 创建表单视图对象，并设置表单的键和名称
        FormVo formVo=new FormVo();
        formVo.setFormKey(startFormInfo.getKey());
        formVo.setFormName(startFormInfo.getName());
        
        // 将表单字段信息从SimpleFormModel转换为FormFieldVo列表
        List<FormField>formFields=((SimpleFormModel)startFormInfo.getFormModel()).getFields();
        List<FormFieldVo>formFieldVos=formFields.stream().map(formField -> 
                new FormFieldVo()
                .setId(formField.getId())
                .setName(formField.getName())
                .setType(formField.getType())
                .setValue(formField.getValue())
                .setReadOnly(formField.isReadOnly())
                .setRequired(formField.isRequired())
            ).collect(Collectors.toList());
        // 将字段信息设置到表单视图对象中
        formVo.setFormFields(formFieldVos);
        return formVo;
    }

    /**
     * 获取指定任务表单数据的方法。
     * 
     * @param taskId 任务的唯一标识符，用于指定需要获取表单数据的任务。
     * @return FormVo 返回任务的表单数据对象，包含了任务表单的相关信息。
     */
    @Override
    public FormVo getTaskFormData(String taskId){
        // 通过operatorService服务获取指定taskId的任务表单数据
        return operatorService.getTaskFormData(taskId);
    }

    /**
     * 根据任务ID获取任务表单。
     * 
     * @param taskId 任务的唯一标识符。
     * @return 返回表单内容的字符串表示。
     * @throws ApiException 如果任务不存在或者表单不存在，则抛出此异常。
     */
    @Override
    public String getTaskForm(String taskId) {
        // 查询指定taskId的任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 如果任务不存在，则抛出异常
        if (task == null) {
            throw new ApiException("任务不存在");
        }

        // 获取任务关联的表单key
        String formKey = task.getFormKey();
        // 如果表单key不存在，则抛出异常
        if (formKey == null) {
            throw new ApiException("表单不存在");
        }
        // 构建表单资源路径
        String path = "/forms/" + formKey;
        ClassPathResource classPathResource = new ClassPathResource(path);
        InputStream inputStream = null;
        String taskForm = null;
        try {
            // 尝试从资源路径加载表单内容
            inputStream = classPathResource.getInputStream();
            taskForm = IOUtils.toString(inputStream, "UTF-8");
            // 将表单内容转换为JSON对象，再转回字符串
            JSONObject jsonobject = JSON.parseObject(taskForm);
            taskForm = jsonobject.toJSONString();
        } catch (Exception e) {
            // 如果加载或转换过程中出现异常，则抛出表单不存在异常
            throw new ApiException("表单不存在");
        }
        return taskForm;
    }

    /**
     * 保存审批任务。
     * 该方法将指定的审批任务及其表单数据保存到系统中。
     * 
     * @param taskId 审批任务的唯一标识符，用于识别具体的审批任务。
     * @param form 包含审批任务表单数据的映射，键值对形式，用于存储任务相关的表单字段及其值。
     * 
     * @see operatorService.saveOperatorTask 该方法实际调用了operatorService的saveOperatorTask方法来完成任务保存。
     */
    @Override
    public void saveApprovalTask(String taskId, Map<String, String> form) {
        operatorService.saveOperatorTask(taskId, form);
    }

    /**
     * 完成指定任务的审批流程。
     * @param taskId 任务的唯一标识符。
     * 该方法首先验证任务是否存在，然后更新流程进度，将当前任务ID添加到表单列表中，
     * 并完成任务，最后插入审批者实体。
     * @throws ApiException 如果任务不存在，则抛出此异常。
     */
    @Override
    public void completeApprovalOwnTask(String taskId) {
        // 根据任务ID查询任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }

        // 更新流程进度，将当前任务ID添加到流程变量"formList"中
        String jsonString=(String)runtimeService.getVariable(task.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(taskId);
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(task.getExecutionId(), "formList", jsonString);

        // 完成任务
        taskService.complete(taskId);
        // 插入审批者实体
        insertApproverEntity(UserContext.getCurrentUserId(), taskId);
    }

    /**
     * 完成审批任务并更新流程状态。
     * @param taskId 任务ID，用于标识需要完成的任务。
     * @param form 包含任务表单数据的Map，键值对形式。
     * 该方法首先验证任务是否存在，然后更新流程变量（包括表单列表和当前处理人），
     * 提交任务表单数据，并记录审批人信息。
     * 若任务不存在，则抛出运行时异常。
     */
    @Override
    public void completeApprovalTask(String taskId, Map<String, String> form) {
        // 根据任务ID查询任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        // 更新流程中的表单列表和当前处理人变量
        String jsonString=(String)runtimeService.getVariable(task.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(taskId);
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(task.getExecutionId(), "formList", jsonString);
        
        // 设置当前处理人的姓名
        String userName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();
        runtimeService.setVariable(task.getExecutionId(), taskId, userName);

        // 提交任务表单数据
        formService.submitTaskFormData(taskId, form);
        // 记录审批人实体
        insertApproverEntity(UserContext.getCurrentUserId(), taskId);
    }

    /**
     * 获取历史任务的详细信息。
     * 
     * @param taskId 任务的ID，用于查询历史任务实例。
     * @return TaskVo 返回任务的详细信息模型对象，包括任务名称、任务ID、执行ID、发起人名称、指派人名称、
     *         流程定义名称、任务描述、截止时间、完成时间等。
     * @throws ApiException 如果指定的任务ID不存在，则抛出此异常。
     */
    @Override
    public TaskVo getHistoricalDetails(String taskId) {
        // 根据任务ID查询历史任务实例
        HistoricTaskInstance task=historyService.createHistoricTaskInstanceQuery()
            .taskId(taskId).singleResult();
        // 如果任务不存在，则抛出异常
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        TaskVo taskVo=new TaskVo();
        // 查询发起人ID，并转换为字符串格式
        String starterId=(String)historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(task.getProcessInstanceId()).variableName("starter")
            .singleResult().getValue();
        // 根据发起人ID查询发起人的名称
        String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
        // 查询流程定义名称
        String processDefinitionName=repositoryService.createProcessDefinitionQuery().processDefinitionId(
            task.getProcessDefinitionId()
        ).singleResult().getName();
        // 查询任务的指派人名称
        String userName=(String)historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(task.getProcessInstanceId()).variableName(taskId)
            .singleResult().getValue();
        // 设置任务Vo对象的各个属性
        taskVo.setTaskId(task.getId())
            .setTaskName(task.getName())
            .setExecutionId(task.getExecutionId())
            .setStarterName(starter)
            .setAssigneeName(userName)
            .setProcessDefinitionName(processDefinitionName)
            .setDescription(task.getDescription());
        // 如果任务有截止时间，则设置截止时间的字符串表示
        if (task.getDueDate() != null) {
            taskVo.setDueTime(task.getDueDate().toString());   
        }
        // 设置任务结束时间的字符串表示
        taskVo.setEndTime(task.getEndTime().toString());
        return taskVo;
    }
}
