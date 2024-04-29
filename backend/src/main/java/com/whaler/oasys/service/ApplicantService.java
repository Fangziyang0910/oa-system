package com.whaler.oasys.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.ApplicantEntity;
import com.whaler.oasys.model.vo.ApplicantVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;

public interface ApplicantService
extends IService<ApplicantEntity> {
    int insertApplicantEntity(Long applicantId, String processinstanceId);

    int deleteApplicantEntity(Long applicantId, String processinstanceId);

    ApplicantVo selectByApplicantId(Long applicantId);

    List<ProcessDefinitionVo> listProcessDefinitions();

    ProcessInstanceVo createProcessInstance(String processDefinitionKey);

    FormVo getStartForm(String processInstanceId);

    void submitStartForm(String processInstanceId, Map<String,String>startForm);

    void getProcessInstanceProgress(String processInstanceId);

    // void abortProcessInstance();

    // void getHistoricalDetails();
}
