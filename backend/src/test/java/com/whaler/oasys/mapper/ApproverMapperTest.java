package com.whaler.oasys.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.whaler.oasys.model.entity.ApproverEntity;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(true)
public class ApproverMapperTest {
    @Autowired
    private ApproverMapper approverMapper;

    @Test
    @Transactional
    public void testInsertApproverEntity() {
        approverMapper.insertApproverEntity(1L, "processinstanceIdxxxx135");
    }

    @Test
    @Transactional
    public void testDeleteApproverEntity() {
        approverMapper.deleteApproverEntity(1L, "processinstanceIdxxxx135");
    }

    @Test
    @Transactional
    public void testSelectByApproverId() {
        approverMapper.insertApproverEntity(1L, "processinstanceIdxxxx135");
        approverMapper.insertApproverEntity(1L, "processinstanceIdxxxx136");
        List<ApproverEntity>approverEntities= approverMapper.selectByApproverId(1L);
        for (ApproverEntity approverEntity : approverEntities) {
            System.out.println(approverEntity);
        }
    }
}
