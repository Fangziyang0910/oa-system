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
public class LeaveProcess2Test {
    @Autowired
    private ApplicantService applicantService;
    @Autowired
    private ApproverService approverService;
    @Autowired
    private OperatorService operatorService;

    @Test
    @Transactional
    public void testDelegate(){
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("leaveProcess2");
        Map<String,String> startForm=new HashMap<>();
        startForm.put("applicantDepartment","研发部");
        applicantService.submitStartForm(processInstanceVo.getProcessInstanceId(), startForm);
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

    private void predo(){
        // 创建流程实例将流程运行到leader审批处
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("leaveProcess2");
        Map<String,String> startForm=new HashMap<>();
        startForm.put("applicantDepartment","研发部");
        applicantService.submitStartForm(processInstanceVo.getProcessInstanceId(), startForm);
    }
    
}
