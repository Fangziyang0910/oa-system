package com.whaler.oasys.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.whaler.oasys.model.entity.PermissionEntity;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PermissionMapperTest {
    @Autowired
    private PermissionMapper permissionMapper;

    @Test
    public void testInsertApplicantEntity(){
        permissionMapper.insertPermissionEntity("研发部", "数据工程师", true, false, true);

        permissionMapper.deleteByDepartmentRole("研发部", "数据工程师");
    }

    @Test
    public void testSelectByDepartment(){
        List<PermissionEntity>permissions=permissionMapper.selectByDepartment("研发部");
        for (PermissionEntity permissionEntity : permissions) {
            System.out.println(permissionEntity);
        }
    }

    @Test
    public void testSelectByRole(){
        List<PermissionEntity>permissions=permissionMapper.selectByRole("部门主管");
        for (PermissionEntity permissionEntity : permissions) {
            System.out.println(permissionEntity);
        }
    }

    @Test
    public void testSelectByIsApplicant(){
        List<PermissionEntity>permissions=permissionMapper.selectByIsApplicant(true);
        for (PermissionEntity permissionEntity : permissions) {
            System.out.println(permissionEntity);
        }
    }

    @Test
    public void testSelectByIsApprover(){
        List<PermissionEntity>permissions=permissionMapper.selectByIsApprover(true);
        for (PermissionEntity permissionEntity : permissions) {
            System.out.println(permissionEntity);
        }
    }

    @Test
    public void testSelectByIsOperator(){
        List<PermissionEntity>permissions=permissionMapper.selectByIsOperator(true);
        for (PermissionEntity permissionEntity : permissions) {
            System.out.println(permissionEntity);
        }
    }

    @Test
    public void testSelectAllDepartments(){
        List<String>departments=permissionMapper.selectAllDepartments();
        for (String department : departments) {
            System.out.println(department);
        }
    }
}
