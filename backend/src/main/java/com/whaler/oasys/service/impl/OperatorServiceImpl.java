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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.OperatorMapper;
import com.whaler.oasys.model.entity.OperatorEntity;
import com.whaler.oasys.model.entity.PermissionEntity;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.vo.CategoryVo;
import com.whaler.oasys.model.vo.FormFieldVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.OperatorVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.CategoryService;
import com.whaler.oasys.service.OperatorService;
import com.whaler.oasys.service.PermissionService;
import com.whaler.oasys.service.UserService;
import com.whaler.oasys.tool.MyMesgSender;

import liquibase.pro.packaged.id;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private MyMesgSender myMesgSender;

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
                taskVo.setOwnerName(task.getOwner());
                taskVo.setAssigneeName(task.getAssignee());
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
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        try{
            taskService.claim(taskId, userName);
            taskService.setOwner(taskId, userName);
        }catch(Exception e){
            throw new ApiException("任务已被签收");
        }
    }

    @Override
    public void unclaimCandidateTask(String taskId) {
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        // 清空已填写的表单信息
        FormInfo taskFormInfo = taskService.getTaskFormModel(taskId);
        if(taskFormInfo==null){
            throw new ApiException("表单不存在");
        }
        List<FormField>formFields=((SimpleFormModel)taskFormInfo.getFormModel()).getFields();
        Map<String,String>nullMap=new HashMap<>();
        formFields.forEach(formField->{
            nullMap.put(formField.getId(), null);
        });
        formService.saveFormData(taskId, nullMap);

        taskService.setAssignee(taskId, null);
        taskService.setOwner(taskId, null);
    }
    
    @Override
    public List<TaskVo> listOperatorAssignTasks() {
        String userName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();

        List<Task>operateTasks=taskService.createTaskQuery().or()
            .taskOwner(userName)
            .taskAssignee(userName).endOr()
            .taskDescription("操作")
            .list();

        List<TaskVo>taskVos=operateTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                taskVo.setOwnerName(task.getOwner());
                taskVo.setAssigneeName(task.getAssignee());
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
    public TaskVo getTaskNotCompleted(String taskId){
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        try{
            String starterId=(String)runtimeService.getVariable(task.getExecutionId(), "starter");
            String starter=userService.selectByUserId(Long.parseLong(starterId)).getName();
            String processDefinitionName=repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId()).singleResult().getName();
            TaskVo taskVo=new TaskVo();
            taskVo.setTaskId(taskId)
                .setTaskName(task.getName())
                .setExecutionId(task.getExecutionId())
                .setStarterName(starter)
                .setOwnerName(task.getOwner())
                .setAssigneeName(task.getAssignee())
                .setProcessDefinitionName(processDefinitionName)
                .setDescription(task.getDescription())
                .setEndTime(null);
            if (task.getDueDate()!=null) {
                taskVo.setDueTime(task.getDueDate().toString());
            }
            return taskVo;
        }catch(Exception e){
            throw new ApiException("任务信息获取异常");
        }
    }

    @Override
    public List<String> listOperatorCandidateUsers(String taskId) {
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        Set<String> userNames=new HashSet<>();
        LambdaQueryWrapper<UserEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        // 获取所有权限
        List<Long>permissionIds=permissionService.list().stream()
            .filter(permission->{
                return permission.getIsOperator();
        }).map(permission->permission.getId()).collect(Collectors.toList());
        List<Long>candidateUsers=new ArrayList<>();
        // 获取候选用户权限
        permissionIds.forEach(
            permissionId->{
                Task tmp=taskService.createTaskQuery()
                    .taskId(taskId)
                    .taskCandidateUser(permissionId.toString()).singleResult();
                if (tmp!=null) {
                    candidateUsers.add(permissionId);
                }
            }
        );
        // 查询拥有候选用户权限的所有用户
        candidateUsers.forEach(
            candidateUser->{
                lambdaQueryWrapper.eq(UserEntity::getPermissionId, candidateUser);
                List<String>names=userService.getBaseMapper().selectList(lambdaQueryWrapper)
                    .stream().map(userEntity->userEntity.getName()).collect(Collectors.toList());
                names.forEach(name->userNames.add(name));
                lambdaQueryWrapper.clear();
            }
        );

        // 获取候选类别
        List<Long>categoryIds=categoryService.list().stream()
            .map(category->category.getId()).collect(Collectors.toList());
        List<Long>candidateGroups=new ArrayList<>();
        categoryIds.forEach(
            categoryId->{
                Task tmp=taskService.createTaskQuery()
                    .taskId(taskId)
                    .taskCategory(categoryId.toString()).singleResult();
                if (tmp!=null) {
                    candidateGroups.add(categoryId);
                }
            }
        );
        // 查询符合候选类别的所有用户权限
        candidateGroups.forEach(
            candidateGroup->{
                List<Long>ids=categoryService.selectCategoryIdsByPermissionId(candidateGroup);
                // 查找符合用户权限的所有用户
                ids.forEach(
                    id->{
                        lambdaQueryWrapper.eq(UserEntity::getPermissionId, id);
                        List<String>names=userService.getBaseMapper().selectList(lambdaQueryWrapper)
                            .stream().map(userEntity->userEntity.getName()).collect(Collectors.toList());
                        names.forEach(name->userNames.add(name));
                        lambdaQueryWrapper.clear();
                    }
                );
            }
        );
        // 如果没有设置候选，返回本部门用户
        if (userNames.size()==0) {
            // 获得部门名称
            String department=userService.selectByUserId(UserContext.getCurrentUserId()).getDepartment();
            LambdaQueryWrapper<PermissionEntity>lambdaQueryWrapper2=new LambdaQueryWrapper<>();
            lambdaQueryWrapper2.eq(PermissionEntity::getDepartment, department)
                .eq(PermissionEntity::getIsOperator, true);
            // 查询该部门所有用户权限
            List<Long> ids=permissionService.getBaseMapper().selectList(lambdaQueryWrapper2)
                .stream().map(permissionEntity->permissionEntity.getId()).collect(Collectors.toList());
            lambdaQueryWrapper2.clear();
            // 查询符合用户权限的所有用户
            ids.forEach(
                id->{
                    lambdaQueryWrapper.eq(UserEntity::getPermissionId, id);
                    List<String>names=userService.getBaseMapper().selectList(lambdaQueryWrapper)
                        .stream().map(userEntity->userEntity.getName()).collect(Collectors.toList());
                    names.forEach(name->userNames.add(name));
                    lambdaQueryWrapper.clear();
                }
            );
        }
        // 删除本人
        String myName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();
        if (userNames.contains(myName)) {
            userNames.remove(myName);
        }
        return userNames.stream().collect(Collectors.toList());
    }

    @Override
    public void assignTask(String taskId, String name) {
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        if (!task.getOwner().equals(task.getAssignee())) {
            throw new ApiException("任务禁止委派");
        }
        taskService.setAssignee(taskId, name);
    }

    @Override
    public void unassignTask(String taskId) {
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        taskService.setAssignee(taskId, task.getOwner());
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
    public FormVo getTaskFormData(String taskId){
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task==null) {
            throw new ApiException("任务不存在");
        }
        FormInfo taskFormInfo = taskService.getTaskFormModel(taskId);
        if(taskFormInfo==null){
            throw new ApiException("表单不存在");
        }
        FormVo formVo=new FormVo();
        formVo.setFormKey(taskFormInfo.getKey());
        formVo.setFormName(taskFormInfo.getName());
        List<FormField>formFields=((SimpleFormModel)taskFormInfo.getFormModel()).getFields();
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
    public void saveOperatorTask(String taskId, Map<String, String> form) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        formService.saveFormData(taskId, form);
    }

    @Override
    public void endAssignedTask(String taskId){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        taskService.setAssignee(taskId, task.getOwner());
        String userName=userService.selectByUserId(UserContext.getCurrentUserId()).getName();
        // 记录协作历史
        String jsonString=(String)runtimeService.getVariable(task.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(task.getName()+"由 "+userName+" 协作中");
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(task.getExecutionId(), "formList", jsonString);

        String msgName="任务协作完成通知";
        String msgContent=String.format(
            "您委派的任务 %s 已经完成，请及时处理！\n",
            task.getName()
        );
        myMesgSender.sendMessage(task.getOwner(), msgName, msgContent);
    }

    @Override
    public void completeOperatorOwnTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }

        // 更新流程进度
        String jsonString=(String)runtimeService.getVariable(task.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(taskId);
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(task.getExecutionId(), "formList", jsonString);

        taskService.complete(taskId);
        insertOperatorEntity(UserContext.getCurrentUserId(), taskId);
    }

    @Override
    public void completeOperatorTask(String taskId, Map<String, String> form) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
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
