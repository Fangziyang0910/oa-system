package com.whaler.oasys.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.whaler.oasys.model.entity.UserEntity;

@RunWith(SpringRunner.class) // 使用Spring的测试运行器
@MybatisPlusTest // Mybatis Plus的测试类
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 配置测试数据库，不替换现有的数据库
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper; // 自动装配UserMapper

    /**
     * 测试UserMapper的基本功能，包括查询和增删操作。
     */
    @Test
    public void test(){
        // 查询用户名为"user1"的用户
        UserEntity userEntity = userMapper.selectByName("user1");
        System.out.println(userEntity);

        // 插入一个新的用户记录
        userMapper.insertUserEntity("user33", "123456", "user33@163.com", "10086", "北京", 1L);
        // 删除刚刚插入的用户记录
        userMapper.deleteByName("user33");
    }
}
