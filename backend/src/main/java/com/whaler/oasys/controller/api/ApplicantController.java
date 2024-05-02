package com.whaler.oasys.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.param.StartFormParam;
import com.whaler.oasys.model.vo.ApplicantVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.model.vo.TaskVo;
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

    @PostMapping("/submitStartForm")
    public void submitStartForm(
        @RequestBody @Validated StartFormParam formParam
    ){
        applicantService.submitStartForm(formParam.getProcessInstanceId(), formParam.getForm());
    }

    @GetMapping("/getProcessInstanceProgress/{processInstanceId}")
    public List<TaskVo> getProcessInstanceProgress(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){
        return applicantService.getProcessInstanceProgress(processInstanceId);
    }

    @GetMapping("/getHistoricalForm/{taskId}")
    public FormVo getHistoricalForm(
        @PathVariable(value = "taskId") String taskId
    ){
        return applicantService.getHistoricalForm(taskId);
    }

    @GetMapping("/getProcessInstance/{processInstanceId}")
    public ProcessInstanceVo getProcessInstance(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){ 
        return applicantService.getProcessInstance(processInstanceId);
    }

    @PostMapping("/abortProcessInstance")
    public void abortProcessInstance(
        @RequestBody @Validated String processInstanceId,
        @RequestBody @Validated String reason
    ){ 
        applicantService.abortProcessInstance(processInstanceId, reason);
    }
}
