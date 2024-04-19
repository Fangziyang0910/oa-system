package com.whaler.oasys.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.whaler.oasys.Main;
import com.whaler.oasys.model.vo.ApplicantVo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
public class ApplicantServiceTest {
    @Autowired
    private ApplicantService applicantService;

    @Test
    @Transactional
    public void testInsertApplicantEntity() {
        int row=applicantService.insertApplicantEntity(1L, "processinstanceIdooxxdd");
        System.out.println(row);
    }

    @Test
    @Transactional
    public void testDeleteApplicantEntity() {
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxdd");
        int row=applicantService.deleteApplicantEntity(1L, "processinstanceIdooxxdd");
        System.out.println(row);
    }

    @Test
    @Transactional
    public void testSelectByApplicantId() {
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxdd");
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxxx");
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxaa");
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxee");
        applicantService.insertApplicantEntity(1L, "processinstanceIdooxxrr");
        ApplicantVo applicantVo=applicantService.selectByApplicantId(1L);
        System.out.println(applicantVo);
    }
}
