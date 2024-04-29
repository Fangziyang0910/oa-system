package com.whaler.oasys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.whaler.oasys.Main;
import com.whaler.oasys.model.vo.ApplicantVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.security.UserContext;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
@Slf4j
public class ApplicantServiceTest {
    @Autowired
    private ApplicantService applicantService;

    @Test
    @Transactional
    public void testInsertApplicantEntity() {
        int row=applicantService.insertApplicantEntity(1L, "processinstanceIdooxxdd");
        System.out.println(row);
    }

    @Test
    @Transactional
    public void testDeleteApplicantEntity() {
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxdd");
        int row=applicantService.deleteApplicantEntity(1L, "processinstanceIdooxxdd");
        System.out.println(row);
    }

    @Test
    @Transactional
    public void testSelectByApplicantId() {
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxdd");
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxxx");
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxaa");
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxee");
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxrr");
        ApplicantVo applicantVo=applicantService.selectByApplicantId(1L);
        System.out.println(applicantVo);
    }

    @Test
    @Transactional
    public void testListProcessDefinitions() {
        List<ProcessDefinitionVo>processDefinitionVos=applicantService.listProcessDefinitions();
        for (ProcessDefinitionVo processDefinitionVo : processDefinitionVos) {
            log.info("processDefinition:{}",processDefinitionVo);
        }
    }

    @Test
    @Transactional
    public void testCreateProcessInstance() {
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("leaveProcess");
        log.info("processInstance:{}",processInstanceVo);
    }

    @Test
    @Transactional
    public void testGetStartForm() {
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("leaveProcess");
        FormVo formVo= applicantService.getStartForm(processInstanceVo.getProcessInstanceId());
        log.info("form:{}",formVo);
    }

    @Test
    @Transactional
    public void testSubmitStartForm() {
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("leaveProcess");
        Map<String,String> startForm=new HashMap<>();
        startForm.put("applicantName", "张三");
        startForm.put("applicantDepartment", "研发部");
        startForm.put("applicantRole", "后端工程师");
        startForm.put("applicantEmail", "10086@qq.com");
        startForm.put("applicantPhone", "10086");
        startForm.put("leaveDays","3");
        startForm.put("leaveReason","回家");
        startForm.put("startTime","2020-01-01");
        startForm.put("endTime","2020-01-04");
        startForm.put("leader","5");
        startForm.put("manager","6");
        applicantService.submitStartForm(processInstanceVo.getProcessInstanceId(), startForm);
    }

}
