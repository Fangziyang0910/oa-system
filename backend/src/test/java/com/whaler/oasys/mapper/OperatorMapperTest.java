package com.whaler.oasys.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.whaler.oasys.model.entity.OperatorEntity;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OperatorMapperTest {
    @Autowired
    private OperatorMapper operatorMapper;

    @Test
    public void testInsertOperatorEntity() {
        operatorMapper.insertOperatorEntity(1L, "processinstanceIdxxxx123");
    }

    @Test
    public void testDeleteOperatorEntity() {
        operatorMapper.deleteOperatorEntity(1L, "processinstanceIdxxxx123");
    }

    @Test
    public void testSelectByOperatorId() {
        operatorMapper.insertOperatorEntity(1L, "processinstanceIdxxxx123");
        operatorMapper.insertOperatorEntity(1L, "processinstanceIdxxxx124");
        List<OperatorEntity> operatorEntities = operatorMapper.selectByOperatorId(1L);
        for (OperatorEntity operatorEntity : operatorEntities) {
            System.out.println(operatorEntity);
        }
    }
}
