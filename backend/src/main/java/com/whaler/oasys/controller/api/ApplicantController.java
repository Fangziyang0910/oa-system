package com.whaler.oasys.controller.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.StartFormParam;
import com.whaler.oasys.model.vo.ApplicantVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/applicant")
@Api(description = "申请人权限")
public class ApplicantController {
    @Autowired
    private ApplicantService applicantService;

    @ApiOperation("申请人查询发起的流程实例")
    @GetMapping("/listProcessInstances")
    public ApplicantVo lisApplicantVo(){
        Long applicantId=UserContext.getCurrentUserId();
        return applicantService.selectByApplicantId(applicantId);
    }

    @ApiOperation("申请人查询所有的流程定义")
    @GetMapping("/listProcessDefinitions")
    public List<ProcessDefinitionVo> listProcessDefinitions(){
        return applicantService.listProcessDefinitions();
    }

    @ApiOperation("申请人创建流程实例")
    @GetMapping("/createProcessInstance/{processDefinitionKey}")
    public ProcessInstanceVo createProcessInstance(
        @PathVariable(value = "processDefinitionKey") String processDefinitionKey
    ){
        return applicantService.createProcessInstance(processDefinitionKey);
    }

    @ApiOperation("申请人查询提交的工单模板")
    @GetMapping("/getStartForm/{processInstanceId}")
    public FormVo getStartForm(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){
        return applicantService.getStartForm(processInstanceId);
    }

    @ApiOperation("申请人提交填写完的工单")
    @PostMapping("/submitStartForm")
    public void submitStartForm(
        @RequestBody @Validated StartFormParam formParam
    ){
        applicantService.submitStartForm(formParam.getProcessInstanceId(), formParam.getForm());
    }

    @ApiOperation("申请人查询流程实例的进度")
    @GetMapping("/getProcessInstanceProgress/{processInstanceId}")
    public List<TaskVo> getProcessInstanceProgress(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){
        return applicantService.getProcessInstanceProgress(processInstanceId);
    }

    @ApiOperation("申请人查询流程任务节点的详细情况")
    @GetMapping("/getHistoricalForm/{taskId}")
    public FormVo getHistoricalForm(
        @PathVariable(value = "taskId") String taskId
    ){
        return applicantService.getHistoricalForm(taskId);
    }

    @ApiOperation("申请人查询流程实例的详细情况")
    @GetMapping("/getProcessInstance/{processInstanceId}")
    public ProcessInstanceVo getProcessInstance(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){ 
        return applicantService.getProcessInstance(processInstanceId);
    }

    @ApiOperation("申请人终止流程实例")
    @PostMapping("/abortProcessInstance")
    public void abortProcessInstance(
        @RequestBody @Validated String processInstanceId,
        @RequestBody @Validated String reason
    ){ 
        applicantService.abortProcessInstance(processInstanceId, reason);
    }

    @ApiOperation("申请人查询流程实例的原始流程图")
    @GetMapping(value = "/getOriginalProcessDiagram/{processDefinitionKey}",
        produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getOriginalProcessDiagram(
        @PathVariable(value = "processDefinitionKey") String processDefinitionKey
    ){ 
        InputStream inputStream = applicantService.getOriginalProcessDiagram(processDefinitionKey);
        byte[] bytes;
        try {
            bytes=new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
        } catch (Exception e) {
            throw new ApiException("获取流程图失败");
        }
        return bytes;
    }

    @ApiOperation("申请人查询流程实例的流程图")
    @GetMapping(value = "/getProcessInstanceDiagram/{processInstanceId}",
        produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getProcessInstanceDiagram(
        @PathVariable(value = "processInstanceId") String processInstanceId
    ){ 
        InputStream inputStream = applicantService.getProcessInstanceDiagram(processInstanceId);
        byte[] bytes;
        try {
            bytes=new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
        } catch (Exception e) {
            throw new ApiException("获取流程图失败");
        }
        return bytes;
    }
}
