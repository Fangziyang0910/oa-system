package com.whaler.oasys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.FormService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormInfo;
import org.flowable.form.api.FormModel;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.ApplicantMapper;
import com.whaler.oasys.model.entity.ApplicantEntity;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.vo.ApplicantVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.FormFieldVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
            .collect(Collectors.toSet())
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
        formService.submitTaskFormData(starterTask.getId(), startForm);
        runtimeService.setVariable(processInstanceId, "startFormTask", starterTask.getId());
    }

    @Override
    public void getProcessInstanceProgress(String processInstanceId){
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if(pi==null){
            throw new ApiException("流程不存在");
        }
    }
}
