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
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;
import com.whaler.oasys.service.ApproverService;
import com.whaler.oasys.service.OperatorService;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
@Slf4j
public class TaskAssignTest {
    @Autowired
    private ApplicantService applicantService;
    @Autowired
    private ApproverService approverService;
    @Autowired
    private OperatorService operatorService;

    @Test
    @Transactional
    public void testListOperatorCandidateTasks(){
        predo();
        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = listOperatorCandidateTasks();
        log.info("listOperatorCandidateTasks:{}",taskVo.toString());
    }

    @Test
    @Transactional
    public void testClaimCandidateTask(){
        predo();
        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = listOperatorCandidateTasks();
        claimCandidateTask(taskVo.getTaskId());
        taskVo = listOperatorAssignTasks();
        log.info("listOperatorAssignTasks:{}",taskVo.toString());
        try{
            taskVo = listOperatorCandidateTasks();
        }catch(Exception e){
            log.info("listOperatorCandidateTasks:null");
        }
    }

    @Test
    @Transactional
    public void testUnclaimCandidateTask(){
        predo();
        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = listOperatorCandidateTasks();
        claimCandidateTask(taskVo.getTaskId());
        unclaimCandidateTask(taskVo.getTaskId());
        try{
            taskVo = listOperatorAssignTasks();
        }catch(Exception e){
            log.info("listOperatorAssignTasks:null");
        }
        taskVo = listOperatorCandidateTasks();
        log.info("listOperatorCandidateTasks:{}",taskVo.toString());
    }

    @Test
    @Transactional
    public void testGetTaskFormData(){
        predo();
        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = listOperatorCandidateTasks();
        claimCandidateTask(taskVo.getTaskId());

        taskVo = listOperatorAssignTasks();
        Map<String,String> form=new HashMap<>();
        form.put("operatorName","user20");
        form.put("operatorEmail","user20@qq.com");
        operatorService.saveOperatorTask(taskVo.getTaskId(), form);
        FormVo formVo = operatorService.getTaskFormData(taskVo.getTaskId());

        log.info("getTaskFormData:{}",formVo.toString());form=new HashMap<>();

        form.put("operatorName","user5");
        form.put("operatorEmail","user5@qq.com");
        operatorService.saveOperatorTask(taskVo.getTaskId(), form);
        formVo = operatorService.getTaskFormData(taskVo.getTaskId());

        log.info("getTaskFormData:{}",formVo.toString());
    }

    @Test
    @Transactional
    public void testAssignTask(){
        predo();
        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = listOperatorCandidateTasks();
        claimCandidateTask(taskVo.getTaskId());
        taskVo=listOperatorAssignTasks();
        log.info("listOperatorAssignTasks:{}",taskVo.toString());

        operatorService.assignTask(taskVo.getTaskId(), "user1");
        taskVo = listOperatorAssignTasks();
        log.info("listOperatorAssignTasks:{}",taskVo.toString());

        UserContext.setCurrentUserId(1L);
        taskVo = listOperatorAssignTasks();
        log.info("listOperatorAssignTasks:{}", taskVo.toString());
    }

    @Test
    @Transactional
    public void testUnassignTask(){
        predo();
        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = listOperatorCandidateTasks();
        claimCandidateTask(taskVo.getTaskId());
        taskVo=listOperatorAssignTasks();
        log.info("listOperatorAssignTasks:{}",taskVo.toString());

        operatorService.assignTask(taskVo.getTaskId(), "user1");
        operatorService.unassignTask(taskVo.getTaskId());
        taskVo=listOperatorAssignTasks();
        log.info("listOperatorAssignTasks:{}",taskVo.toString());
    }

    @Test
    @Transactional
    public void testEndAssignedTask(){
        predo();
        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = listOperatorCandidateTasks();
        claimCandidateTask(taskVo.getTaskId());
        operatorService.assignTask(taskVo.getTaskId(), "user1");

        UserContext.setCurrentUserId(1L);
        taskVo=listOperatorAssignTasks();
        log.info("listOperatorAssignTasks:{}",taskVo.toString());
        operatorService.endAssignedTask(taskVo.getTaskId());
        try{
            taskVo=listOperatorAssignTasks();
        }catch(Exception e){
            log.info("listOperatorAssignTasks:null");
        }
    }

    private void predo(){
        // 创建流程实例将流程运行到leader审批处
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("taskAssignProcess");
        Map<String,String> form=new HashMap<>();
        form.put("applicantDepartment","研发部");
        // form.put("leader","5");
        // form.put("manager","6");
        applicantService.submitStartForm(processInstanceVo.getProcessInstanceId(), form);

        UserContext.setCurrentUserId(5L);
        form.clear();
        form.put("isLeaderApproval", "true");
        TaskVo taskVo = approverService.listApprovalTasks().get(0);
        approverService.completeApprovalTask(taskVo.getTaskId(), form);

        UserContext.setCurrentUserId(6L);
        form.clear();
        form.put("isManagerApproval", "true");
        taskVo = approverService.listApprovalTasks().get(0);
        approverService.completeApprovalTask(taskVo.getTaskId(), form);
    }

    private TaskVo listOperatorCandidateTasks(){
        List<TaskVo> taskVos = operatorService.listOperatorCandidateTasks();
        return taskVos.get(0);
    }
    
    private void claimCandidateTask(String taskId){
        operatorService.claimCandidateTask(taskId);
    }

    private TaskVo listOperatorAssignTasks(){
        List<TaskVo> taskVo = operatorService.listOperatorAssignTasks();
        return taskVo.get(0);
    }

    private void unclaimCandidateTask(String taskId){
        operatorService.unclaimCandidateTask(taskId);
    }
}
