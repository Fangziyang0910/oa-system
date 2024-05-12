package com.whaler.oasys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.FormService;
import org.flowable.engine.TaskService;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.whaler.oasys.Main;
import com.whaler.oasys.model.vo.FormVo;
import com.whaler.oasys.model.vo.OperatorVo;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.model.vo.TaskVo;
import com.whaler.oasys.security.UserContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
public class OperatorServiceTest {
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ApplicantService applicantService;
    @Autowired
    private ApproverService approverService;

    @Test
    @Transactional
    public void testInsertOperatorEntity() {
        int row = operatorService.insertOperatorEntity(1L, "processinstanceIdxxxxxxxx");
        System.out.println(row);
    }

    @Test
    @Transactional
    public void testDeleteOperatorEntity() {
        int row = operatorService.deleteOperatorEntity(1L, "processinstanceIdxxxxxxxx");
        System.out.println(row);
    }

    @Test
    @Transactional
    public void testSelectByOperatorId() {
        operatorService.deleteOperatorEntity(1L, "processinstanceIdxxxxxxxx");
        operatorService.deleteOperatorEntity(1L, "processinstanceIdxxxxxxww");
        operatorService.deleteOperatorEntity(1L, "processinstanceIdxxxxxxee");
        operatorService.deleteOperatorEntity(1L, "processinstanceIdxxxxxxrr");
        operatorService.deleteOperatorEntity(1L, "processinstanceIdxxxxxxtt");
        OperatorVo operatorVo = operatorService.selectByOperatorId(1L);
        System.out.println(operatorVo);
    }
    
    @Test
    @Transactional
    public void testListOperatorTasks(){
        predo();
        // 测试leader审批任务查询
        UserContext.setCurrentUserId(20L);
        List<TaskVo> taskVos = operatorService.listOperatorTasks();
        log.info("taskVos:{}",taskVos);
    }

    @Test
    @Transactional
    public void getStartForm(){
        predo();

        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = operatorService.listOperatorTasks().get(0);
        FormVo formVo = operatorService.getStartForm(taskVo.getTaskId());
        log.info("formVo:{}",formVo);
    }

    @Test
    @Transactional
    public void getTaskForm(){
        predo();

        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = operatorService.listOperatorTasks().get(0);
        String formVo = operatorService.getTaskForm(taskVo.getTaskId());
        log.info("formVo:{}",formVo);
    }

    @Test
    @Transactional
    public void finishOperatorTask(){
        predo();

        UserContext.setCurrentUserId(20L);
        TaskVo taskVo = operatorService.listOperatorTasks().get(0);
        Map<String,String> form = new HashMap<>();
        form.put("operatorName","马冬梅");
        form.put("leaveFile","人事部同意大傻春的请假申请");
        operatorService.completeOperatorTask(taskVo.getTaskId(),form);
        SimpleFormModel formFields=(SimpleFormModel)taskService.getTaskFormModel(taskVo.getTaskId()).getFormModel();
        for(FormField formField:formFields.getFields()){
            log.info(formField.getId()+" "+formField.getName()+" "+formField.getValue());
        }
    }

    private void predo(){
        // 创建流程实例将流程运行到leader审批处
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("leaveProcess");
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
}
