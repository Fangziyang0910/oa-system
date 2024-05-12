package com.whaler.oasys.process;

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
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;
import com.whaler.oasys.service.ApproverService;
import com.whaler.oasys.service.OperatorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
public class ProductSoftwareFailureTest {
    @Autowired
    private ApplicantService applicantService;
    @Autowired
    private ApproverService approverService;
    @Autowired
    private OperatorService operatorService;

    @Test
    @Transactional
    public void testProductSoftwareFailure(){
        log.info("----------------doFailureReport----------------");
        doFailureReport();
        getProcessProgress();
        log.info("----------------doMaintainerApproval----------------");
        doMaintainerApproval();
        getProcessProgress();
        log.info("----------------doDeveloperOperation----------------");
        doDeveloperOperation();
        getProcessProgress();
        // log.info("----------------doTesterOperation----------------");
        // doTesterOperation();
        // getProcessProgress();
        log.info("----------------doMaintainerOperation----------------");
        doMaintainerOperation();
        getProcessProgress();
        log.info("----------------doMaintainerFeedback----------------");
        doMaintainerFeedback();
        getProcessProgress();
    }

    @Test
    @Transactional
    public void testProductSoftwareFailure2(){
        log.info("----------------doFailureReport----------------");
        doFailureReport();
        getProcessProgress();
        log.info("----------------doMaintainerApproval2----------------");
        doMaintainerApproval2();
        getProcessProgress();
        log.info("----------------doMaintainerFeedback----------------");
        doMaintainerFeedback();
        getProcessProgress();

    }

    private void doFailureReport(){
        UserContext.setCurrentUserId(23L);
        applicantService.createProcessInstance("productSoftwareFailure");
        String processInstanceId=applicantService.selectByApplicantId(UserContext.getCurrentUserId()).getProcessinstanceIds().get(0);
        Map<String,String>vars=new HashMap<>();
        vars.put("applicantId", Long.toString(UserContext.getCurrentUserId()));
        applicantService.submitStartForm(processInstanceId,vars);
    }

    private void doMaintainerApproval(){
        UserContext.setCurrentUserId(11L);
        String taskId=approverService.listApprovalTasks().get(0).getTaskId();
        Map<String,String>vars=new HashMap<>();
        vars.put("approverId", Long.toString(UserContext.getCurrentUserId()));
        vars.put("isMaintainerApproval", Boolean.toString(true));
        vars.put("isDeveloperOperation", Boolean.toString(true));
        vars.put("isTesterOperation", Boolean.toString(false));
        vars.put("isMaintainerOperation", Boolean.toString(true));
        approverService.completeApprovalTask(taskId, vars);
    }

    private void doMaintainerApproval2(){
        UserContext.setCurrentUserId(11L);
        String taskId=approverService.listApprovalTasks().get(0).getTaskId();
        Map<String,String>vars=new HashMap<>();
        vars.put("approverId", Long.toString(UserContext.getCurrentUserId()));
        vars.put("isMaintainerApproval", Boolean.toString(false));
        approverService.completeApprovalTask(taskId, vars);
    }

    private void doDeveloperOperation(){
        UserContext.setCurrentUserId(1L);
        String taskId=operatorService.listOperatorTasks().get(0).getTaskId();
        Map<String,String>vars=new HashMap<>();
        vars.put("developerId", Long.toString(UserContext.getCurrentUserId()));
        operatorService.completeOperatorTask(taskId, vars);
    }
    private void doTesterOperation(){
        UserContext.setCurrentUserId(7L);
        String taskId=operatorService.listOperatorTasks().get(0).getTaskId();
        Map<String,String>vars=new HashMap<>();
        vars.put("testerId", Long.toString(UserContext.getCurrentUserId()));
        operatorService.completeOperatorTask(taskId, vars);
    }
    private void doMaintainerOperation(){
        UserContext.setCurrentUserId(12L);
        String taskId=operatorService.listOperatorTasks().get(0).getTaskId();
        Map<String,String>vars=new HashMap<>();
        vars.put("maintainerId", Long.toString(UserContext.getCurrentUserId()));
        operatorService.completeOperatorTask(taskId, vars);
    }

    private void doMaintainerFeedback(){
        UserContext.setCurrentUserId(11L);
        String taskId=operatorService.listOperatorTasks().get(0).getTaskId();
        Map<String,String>vars=new HashMap<>();
        vars.put("maintainerId", Long.toString(UserContext.getCurrentUserId()));
        operatorService.completeOperatorTask(taskId, vars);
    }

    private void getProcessProgress(){
        UserContext.setCurrentUserId(23L);
        String processInstanceId=applicantService.selectByApplicantId(UserContext.getCurrentUserId()).getProcessinstanceIds().get(0);
        List<TaskVo>taskVos=applicantService.getProcessInstanceProgress(processInstanceId);
        log.info("taskVos:{}",taskVos);
        log.info("----------------------------------------------");
    }
}
