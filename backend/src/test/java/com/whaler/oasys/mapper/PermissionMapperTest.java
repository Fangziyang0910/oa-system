package com.whaler.oasys.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PermissionMapperTest {
    @Autowired
    private PermissionMapper permissionMapper;

    @Test
    public void test(){
        permissionMapper.insertPermissionEntity("研发部", "数据工程师", true, false, true);

        permissionMapper.deleteByDepartmentRole("研发部", "数据工程师");
    }
}
