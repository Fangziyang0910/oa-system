package com.whaler.oasys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.whaler.oasys.Main;
import com.whaler.oasys.model.entity.ApproverEntity;
import com.whaler.oasys.model.vo.ApproverVo;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
public class ApproverServiceTest {
    @Autowired
    private ApproverService approverService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ApplicantService applicantService;

    @Test
    @Transactional
    public void testInsertApproverEntity() {
        int row=approverService.insertApproverEntity(1L,"processinstanceIdxxxxss");
        System.out.println(row);
    }

    @Test
    @Transactional
    public void testDeleteApproverEntity() {
        int row=approverService.deleteApproverEntity(1L,"processinstanceIdxxxxss");
        System.out.println(row);
    }

    @Test
    @Transactional
    public void testSelectByApproverId() {
        approverService.insertApproverEntity(1L,"processinstanceIdxxxxss");
        approverService.insertApproverEntity(1L,"processinstanceIdxxxxaa");
        approverService.insertApproverEntity(1L,"processinstanceIdxxxxdd");
        approverService.insertApproverEntity(1L,"processinstanceIdxxxxff");
        ApproverVo approverVo = approverService.selectByApproverId(1L);
        System.out.println(approverVo);
    }

    @Test
    @Transactional
    public void testListApprovalTasks() {
        predo();

        // 测试leader审批任务查询
        UserContext.setCurrentUserId(5L);
        List<TaskVo> taskVos = approverService.listApprovalTasks();
        log.info("taskVos:{}",taskVos);
    }

    
    @Test
    @Transactional
    public void testGetStartForm(){
        predo();
        // 测试leader审批任务查询
        UserContext.setCurrentUserId(5L);
        TaskVo taskVo = approverService.listApprovalTasks().get(0);

        FormVo formVo = approverService.getStartForm(taskVo.getTaskId());
        log.info("formVo:{}",formVo);
        
    }

    @Test
    @Transactional
    public void testGetTaskForm(){
        predo();
        // 测试leader审批任务查询
        UserContext.setCurrentUserId(5L);
        TaskVo taskVo = approverService.listApprovalTasks().get(0);

        String formVo = approverService.getTaskForm(taskVo.getTaskId());
        log.info("formVo:{}",formVo);
    }

    @Test
    @Transactional
    public void testApprovalTask(){
        predo();
        // 测试leader审批任务查询
        UserContext.setCurrentUserId(5L);
        TaskVo taskVo = approverService.listApprovalTasks().get(0);

        Map<String, String> form = new HashMap<>();
        form.put("isLeaderApproval","true");
        approverService.finishApprovalTask(taskVo.getTaskId(), form);
    }

    @Test
    @Transactional
    public void testGetHistoricalDetails(){
        predo();
        UserContext.setCurrentUserId(5L);
        List<String>taskIds=approverService.selectByApproverId(5L).getProcessinstanceIds();
        log.info("taskIds:{}",taskIds);
        // 测试leader审批任务查询
        TaskVo taskVo = approverService.listApprovalTasks().get(0);
        Map<String, String> form = new HashMap<>();
        form.put("isLeaderApproval","true");
        approverService.finishApprovalTask(taskVo.getTaskId(), form);

        taskIds=approverService.selectByApproverId(5L).getProcessinstanceIds();
        log.info("taskIds:{}",taskIds);
    }

    private void predo(){
        // 创建流程实例将流程运行到leader审批处
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("leaveProcess");
        Map<String,String> startForm=new HashMap<>();
        startForm.put("applicantName","大傻春");
        startForm.put("applicantDepartment","研发部");
        // startForm.put("leader","5");
        // startForm.put("manager","6");
        applicantService.submitStartForm(processInstanceVo.getProcessInstanceId(), startForm);
    }
}
