package com.whaler.oasys.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whaler.oasys.Main;
import com.whaler.oasys.model.entity.PermissionEntity;
import com.whaler.oasys.model.param.PermissionParam;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
public class PermissionServiceTest {
    @Autowired
    private PermissionService permissionService;
    @Test
    @Transactional
    public void testCRUD(){
        LambdaQueryWrapper<PermissionEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<String>departments=Arrays.asList("研发部","财务部门","行政部");
        lambdaQueryWrapper.in(PermissionEntity::getDepartment, departments)
            .eq(PermissionEntity::getIsApplicant, true)
            .eq(PermissionEntity::getIsApprover, true);
        List<PermissionEntity> list=permissionService.list(lambdaQueryWrapper);
        for (PermissionEntity permissionEntity : list) {
            System.out.println(permissionEntity);
        }

    }

    @Test
    @Transactional
    public void testInsert(){
        PermissionParam permissionParam=new PermissionParam();
        permissionParam.setDepartment("研发部")
            .setRole("数据工程师")
            .setIsApplicant(true)
            .setIsApprover(false)
            .setIsOperator(true);
        permissionService.insertPermissionEntity(permissionParam);

        try{
            permissionParam.setRole("后端工程师");
            permissionService.insertPermissionEntity(permissionParam);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
