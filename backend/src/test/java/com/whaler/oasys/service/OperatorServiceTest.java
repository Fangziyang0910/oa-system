package com.whaler.oasys.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.whaler.oasys.Main;
import com.whaler.oasys.model.vo.OperatorVo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
public class OperatorServiceTest {
    @Autowired
    private OperatorService operatorService;

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
}
