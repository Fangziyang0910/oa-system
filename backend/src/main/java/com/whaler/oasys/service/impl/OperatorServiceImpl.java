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
import com.whaler.oasys.mapper.OperatorMapper;
import com.whaler.oasys.model.entity.OperatorEntity;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.vo.FormFieldVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.OperatorVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.CategoryService;
import com.whaler.oasys.service.OperatorService;
import com.whaler.oasys.service.UserService;

@Service
public class OperatorServiceImpl
extends ServiceImpl<OperatorMapper,OperatorEntity>
implements OperatorService {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public int insertOperatorEntity(Long operatorId, String processinstanceId) {
        return this.baseMapper.insertOperatorEntity(operatorId, processinstanceId);
    }

    @Override
    public int deleteOperatorEntity(Long operatorId, String processinstanceId) {
        return this.baseMapper.deleteOperatorEntity(operatorId, processinstanceId);
    }

    @Override
    public OperatorVo selectByOperatorId(Long operatorId) {
        List<OperatorEntity> operatorEntities = this.baseMapper.selectByOperatorId(operatorId);
        OperatorVo operatorVo=new OperatorVo();
        operatorVo.setOperatorId(operatorId);
        operatorVo.setTaskIds(
            operatorEntities.stream()
            .map(OperatorEntity::getProcessinstanceId)
            .collect(Collectors.toList())
        );
        return operatorVo;
    }

    @Override
    public List<TaskVo> listOperatorTasks() {
        Long userId=UserContext.getCurrentUserId();
        String userName=userService.selectByUserId(userId).getName();
        Long permissionId=userService.getById(UserContext.getCurrentUserId()).getPermissionId();
        List<String> categoryIds=categoryService.selectCategoryIdsByPermissionId(permissionId)
            .stream().map(categoryId->Long.toString(categoryId)).collect(Collectors.toList());

        List<Task>operateTasks=taskService.createTaskQuery().or()
            .taskAssignee(userName)
            .taskCandidateUser(Long.toString(permissionId))
            .taskCandidateGroupIn(categoryIds).endOr()
            .taskDescription("操作")
            .list();

        List<TaskVo>taskVos=operateTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
                String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
                taskVo.setStarterName(starter);
                String processDefinitionName=repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId()).singleResult().getName();
                taskVo.setProcessDefinitionName(processDefinitionName);
                taskVo.setDescription(task.getDescription());
                if (task.getDueDate() != null) {
                    taskVo.setDueTime(task.getDueDate().toString());   
                }
                return taskVo;
            }).collect(Collectors.toList());
        return taskVos;
    }

    @Override
    public List<TaskVo> listOperatorCandidateTasks() {
        Long permissionId=userService.getById(UserContext.getCurrentUserId()).getPermissionId();
        List<String> categoryIds=categoryService.selectCategoryIdsByPermissionId(permissionId)
            .stream().map(categoryId->Long.toString(categoryId)).collect(Collectors.toList());

        List<Task>operateTasks=taskService.createTaskQuery().or()
            .taskCandidateUser(Long.toString(permissionId))
            .taskCandidateGroupIn(categoryIds).endOr()
            .taskDescription("操作")
            .list();

        List<TaskVo>taskVos=operateTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
                String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
                taskVo.setStarterName(starter);
                String processDefinitionName=repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId()).singleResult().getName();
                taskVo.setProcessDefinitionName(processDefinitionName);
                taskVo.setDescription(task.getDescription());
                if (task.getDueDate() != null) {
                    taskVo.setDueTime(task.getDueDate().toString());   
                }
                return taskVo;
            }).collect(Collectors.toList());
        return taskVos;

    }

    @Override
    public void claimCandidateTask(String taskId) {
        String userName=userService.getById(UserContext.getCurrentUserId()).getName();
        try{
            taskService.claim(taskId, userName);
        }catch(Exception e){
            throw new ApiException("任务已被签收");
        }
    }

    @Override
    public void unclaimCandidateTask(String taskId, String userName) {
        try{
            taskService.setAssignee(taskId, userName);
        }catch(Exception e){
            throw new ApiException("任务不存在");
        }
    }
    
    @Override
    public List<TaskVo> listOperatorAssignTasks() {
        String userName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();

        List<Task>operateTasks=taskService.createTaskQuery()
            .taskAssignee(userName)
            .taskDescription("操作")
            .list();

        List<TaskVo>taskVos=operateTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
                String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
                taskVo.setStarterName(starter);
                String processDefinitionName=repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId()).singleResult().getName();
                taskVo.setProcessDefinitionName(processDefinitionName);
                taskVo.setDescription(task.getDescription());
                if (task.getDueDate() != null) {
                    taskVo.setDueTime(task.getDueDate().toString());   
                }
                return taskVo;
            }).collect(Collectors.toList());
        return taskVos;

    }

    @Override
    public FormVo getStartForm(String taskId) {
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        String startFormTaskId=runtimeService.getVariable(task.getExecutionId(), "startFormTask").toString();
        
        FormInfo startFormInfo = taskService.getTaskFormModel(startFormTaskId);
        if(startFormInfo==null){
            throw new ApiException("表单不存在");
        }
        FormVo formVo=new FormVo();
        formVo.setFormKey(startFormInfo.getKey());
        formVo.setFormName(startFormInfo.getName());
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
        formVo.setFormFields(formFieldVos);
        return formVo;
    }

    @Override
    public String getTaskForm(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }

        String formKey=task.getFormKey();
        if (formKey==null) {
            throw new ApiException("表单不存在");
        }
        String path="/forms/"+formKey;
        ClassPathResource classPathResource = new ClassPathResource(path);
        InputStream inputStream=null;
        String taskForm=null;
        try{
            inputStream= classPathResource.getInputStream();
            taskForm = IOUtils.toString(inputStream, "UTF-8");
            JSONObject jsonobject = JSON.parseObject(taskForm);
            taskForm = jsonobject.toJSONString();
        }catch(Exception e){
            throw new ApiException("表单不存在");
        }
        return taskForm;
    }

    @Override
    public void completeOperatorTask(String taskId, Map<String, String> form) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        // 更新流程进度
        String jsonString=(String)runtimeService.getVariable(task.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(taskId);
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(task.getExecutionId(), "formList", jsonString);
        String userName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();
        runtimeService.setVariable(task.getExecutionId(), taskId, userName);

        formService.submitTaskFormData(taskId, form);
        insertOperatorEntity(UserContext.getCurrentUserId(), taskId);
    }

    @Override
    public TaskVo getHistoricalDetails(String taskId) {
        HistoricTaskInstance task=historyService.createHistoricTaskInstanceQuery()
            .taskId(taskId).singleResult();
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        TaskVo taskVo=new TaskVo();

        String starterId=(String)historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(task.getProcessInstanceId()).variableName("starter").singleResult()
            .getValue();
        String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
        String processDefinitionName=repositoryService.createProcessDefinitionQuery().processDefinitionId(
            task.getProcessDefinitionId()
        ).singleResult().getName();
        String userName=(String)historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(task.getProcessInstanceId()).variableName(taskId)
            .singleResult().getValue();
        taskVo.setTaskId(task.getId())
            .setTaskName(task.getName())
            .setExecutionId(task.getExecutionId())
            .setStarterName(starter)
            .setAssigneeName(userName)
            .setProcessDefinitionName(processDefinitionName)
            .setDescription(task.getDescription());
        if (task.getDueDate() != null) {
            taskVo.setDueTime(task.getDueDate().toString());   
        }
        taskVo.setEndTime(task.getEndTime().toString());
        return taskVo;
    }
}
