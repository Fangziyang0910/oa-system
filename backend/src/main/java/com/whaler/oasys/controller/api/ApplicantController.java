package com.whaler.oasys.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.vo.ApplicantVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;

@RestController
@RequestMapping("/applicant")
public class ApplicantController {
    @Autowired
    private ApplicantService applicantService;

    @GetMapping("/listProcessInstances")
    public ApplicantVo lisApplicantVo(){
        Long applicantId=UserContext.getCurrentUserId();
        return applicantService.selectByApplicantId(applicantId);
    }

    @GetMapping("/listProcessDefinitions")
    public List<ProcessDefinitionVo> listProcessDefinitions(){
        return applicantService.listProcessDefinitions();
    }

    @GetMapping("/createProcessInstance/{processDefinitionKey}")
    public ProcessInstanceVo createProcessInstance(
        @PathVariable(value = "processDefinitionKey") String processDefinitionKey
    ){
        return applicantService.createProcessInstance(processDefinitionKey);
    }

    @GetMapping("/getStartForm/{processInstanceId}")
    public FormVo getStartForm(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){
        return applicantService.getStartForm(processInstanceId);
    }

    
}
