package com.whaler.oasys.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.whaler.oasys.model.entity.AdministratorEntity;
import com.whaler.oasys.model.entity.ApplicantEntity;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AdministratorMapperTest {
    @Autowired
    private AdministratorMapper administratorMapper;

    @Test
    public void testSelectByName() {
        String name = "admin1";
        AdministratorEntity administratorEntity = administratorMapper.selectByName(name);
        System.out.println(administratorEntity);
    }
}
