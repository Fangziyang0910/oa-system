package com.whaler.oasys.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.impl.form.FormData;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormInfo;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
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
import com.whaler.oasys.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
    private FormService formService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public int insertApproverEntity(Long approverId, String processinstanceId) {
        return this.baseMapper.insertApproverEntity(approverId, processinstanceId);
    }

    @Override
    public int deleteApproverEntity(Long approverId, String processinstanceId) {
        return this.baseMapper.deleteApproverEntity(approverId, processinstanceId);
    }

    @Override
    public ApproverVo selectByApproverId(Long approverId) {
        List<ApproverEntity> approverEntities = this.baseMapper.selectByApproverId(approverId);
        ApproverVo approverVo = new ApproverVo();
        approverVo.setApproverId(approverId);
        approverVo.setProcessinstanceIds(
            approverEntities.stream()
            .map(ApproverEntity::getProcessinstanceId)
            .collect(Collectors.toSet())
        );
        return approverVo;
    }

    @Override
    public List<TaskVo> listApprovalTasks() {
        Long permissionId=userService.getById(UserContext.getCurrentUserId()).getPermissionId();
        List<Task>approvalAssignedTasks=taskService.createTaskQuery()
            .taskAssignee(Long.toString(permissionId)).list();
        
        List<String> categoryIds=categoryService.selectCategoryIdsByPermissionId(permissionId)
            .stream().map(categoryId->Long.toString(categoryId)).collect(Collectors.toList());
        
        List<Task>approvalCandidateTasks=taskService.createTaskQuery()
            .taskCandidateGroupIn(categoryIds).list();

        approvalAssignedTasks.addAll(approvalCandidateTasks);
        List<TaskVo>taskVos=approvalAssignedTasks.stream()
            .map(task->{
                TaskVo taskVo=new TaskVo();
                taskVo.setTaskId(task.getId());
                taskVo.setTaskName(task.getName());
                taskVo.setExecutionId(task.getExecutionId());
                taskVo.setDescription(task.getDescription());
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
    public FormVo getTaskForm(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ApiException("任务不存在");
        }
        FormInfo formInfo = taskService.getTaskFormModel(task.getId());
        if(formInfo==null){
            throw new ApiException("表单不存在");
        }
        FormVo formVo=new FormVo();
        formVo.setFormKey(formInfo.getKey());
        formVo.setFormName(formInfo.getName());
        List<FormField>formFields=((SimpleFormModel)formInfo.getFormModel()).getFields();
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
    public void finishApprovalTask(String taskId, Map<String, String> form) {
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

        formService.submitTaskFormData(taskId, form);
    }

}
