package com.whaler.oasys.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.whaler.oasys.Main;
import com.whaler.oasys.model.vo.ApproverVo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
public class ApproverServiceTest {
    @Autowired
    private ApproverService approverService;

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
}
