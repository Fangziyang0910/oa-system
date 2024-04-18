package com.whaler.oasys.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.whaler.oasys.model.entity.ApplicantEntity;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ApplicantMapperTest {
    @Autowired
    private ApplicantMapper applicantMapper;

    @Test
    public void testInsertApplicantEntity() {
        applicantMapper.insertApplicantEntity(
            1L,
            "processInstanceIdxx2ll33"
        );
    }

    @Test
    public void testDeleteApplicantEntity() {
        applicantMapper.insertApplicantEntity(
            1L,
            "processInstanceIdxx2ll33"
        );
        applicantMapper.deleteApplicantEntity(
            1L,
            "processInstanceIdxx2ll33"
        );
    }

    @Test
    public void testSelectByApplicantId() {
        // 默认会事务回滚
        applicantMapper.insertApplicantEntity(
            1L,
            "processInstanceIdxx2ll33"
        );
        applicantMapper.insertApplicantEntity(
            1L,
            "processInstanceIdxx2ll23"
        );
        List<ApplicantEntity> applicantEntities=applicantMapper.selectByApplicantId(1L);
        for (ApplicantEntity applicantEntity : applicantEntities) {
            System.out.println(applicantEntity);
        }
    }

}
