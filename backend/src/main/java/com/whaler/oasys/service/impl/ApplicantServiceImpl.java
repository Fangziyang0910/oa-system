package com.whaler.oasys.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormInfo;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
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

    @Override
    public int insertApplicantEntity(Long applicantId, String processinstanceId) {
        return this.baseMapper.insertApplicantEntity(applicantId, processinstanceId);
    }

    @Override
    public int deleteApplicantEntity(Long applicantId, String processinstanceId) {
        return this.baseMapper.deleteApplicantEntity(applicantId, processinstanceId);
    }

    @Override
    public ApplicantVo selectByApplicantId(Long applicantId) {
        List<ApplicantEntity> applicantEntities = this.baseMapper.selectByApplicantId(applicantId);
        ApplicantVo applicantVo = new ApplicantVo();
        applicantVo.setApplicantId(applicantId);
        applicantVo.setProcessinstanceIds(
            applicantEntities.stream().map(ApplicantEntity::getProcessinstanceId)
            .collect(Collectors.toList())
        );
        return applicantVo;
    }

    @Override
    public List<ProcessDefinitionVo> listProcessDefinitions() {
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

    @Override
    public ProcessInstanceVo createProcessInstance(String processDefinitionKey) {
        String starterId = Long.toString(UserContext.getCurrentUserId());
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).singleResult();
        if(pd==null){
            throw new ApiException("流程定义不存在");
        }
        Map<String,Object>vars=new HashMap<>();
        vars.put("starter", starterId);
        List<String> formList=new ArrayList<>();
        vars.put("formList", JSON.toJSONString(formList));
        ProcessInstance pi=runtimeService.startProcessInstanceByKey(processDefinitionKey, vars);
        if (pi==null) {
            throw new ApiException("流程实例创建失败");
        }
        insertApplicantEntity(UserContext.getCurrentUserId(), pi.getProcessInstanceId());
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

    @Override
    public FormVo getStartForm(String processInstanceId) {
        String starterId = Long.toString(UserContext.getCurrentUserId());
        Task starterTask=taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .taskAssignee(starterId).singleResult();
        if(starterTask==null){
            throw new ApiException("已提交申请表");
        }
        FormInfo startFormInfo = taskService.getTaskFormModel(starterTask.getId());
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
    public void submitStartForm(String processInstanceId, Map<String, String> startForm) {
        String starterId = Long.toString(UserContext.getCurrentUserId());
        Task starterTask=taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .taskAssignee(starterId).singleResult();
        if(starterTask==null){
            throw new ApiException("已提交申请表");
        }
        
        // 更新流程进度
        String jsonString=(String)runtimeService.getVariable(starterTask.getExecutionId(), "formList");
        List<String>formList=JSON.parseArray(jsonString, String.class);
        formList.add(starterTask.getId());
        jsonString=JSON.toJSONString(formList);
        runtimeService.setVariable(starterTask.getExecutionId(), "formList", jsonString);

        // 提交申请表单
        formService.submitTaskFormData(starterTask.getId(), startForm);
        runtimeService.setVariable(processInstanceId, "startFormTask", starterTask.getId());
    }

    @Override
    public List<TaskVo> getProcessInstanceProgress(String processInstanceId){
        HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
        if(pi==null){
            throw new ApiException("流程不存在");
        }
        String jsonString=(String)historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(processInstanceId)
            .variableName("formList")
            .singleResult().getValue();
        List<String>formList=JSON.parseArray(jsonString, String.class);
        if (formList==null) {
            throw new ApiException("formList 不存在");
        }
        List<TaskVo>taskVos=formList.stream().map(
            taskId->{
                HistoricTaskInstance task=historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                return new TaskVo().setTaskId(task.getId())
                    .setTaskName(task.getName())
                    .setExecutionId(task.getExecutionId())
                    .setDescription(task.getDescription())
                    .setEndTime(task.getEndTime().toString());
            }
        ).collect(Collectors.toList());
        return taskVos;
    }

    @Override
    public FormVo getHistoricalForm(String taskId) {
        HistoricTaskInstance task=historyService.createHistoricTaskInstanceQuery()
            .taskId(taskId)
            .singleResult();
        if (task==null) {
            throw new ApiException("历史任务不存在");
        }
        FormInfo formInfo=taskService.getTaskFormModel(taskId);
        SimpleFormModel simpleFormModel=(SimpleFormModel)formInfo.getFormModel();
        FormVo formVo=new FormVo();
        formVo.setFormKey(formInfo.getKey());
        formVo.setFormName(formInfo.getName());
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

    @Override
    public ProcessInstanceVo getProcessInstance(String processInstanceId) {
        HistoricProcessInstance pi=historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        if (pi==null) {
            throw new ApiException("流程实例不存在");
        }
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(pi.getProcessDefinitionId()).singleResult();
        List<TaskVo> progress=getProcessInstanceProgress(processInstanceId);
        ProcessInstanceVo processInstanceVo=new ProcessInstanceVo();
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
        if (pi.getEndTime()==null) {
            processInstanceVo.setIsCompeleted(false);
        }else{
            processInstanceVo.setEndTime(pi.getEndTime().toString());
            processInstanceVo.setIsCompeleted(true);
        }
        return processInstanceVo;
    }

    @Override
    public void abortProcessInstance(String processInstanceId, String reason) {
        HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (pi==null) {
            throw new ApiException("流程实例不存在");
        }
        if(pi.getEndTime()!=null){
            throw new ApiException("流程实例已经结束");
        }
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }
}
