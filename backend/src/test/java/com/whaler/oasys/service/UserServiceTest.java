package com.whaler.oasys.service;

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
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.param.UserParam;
import com.whaler.oasys.model.vo.UserVo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
public class UserServiceTest {
    @Autowired
    UserService userService;

    @Test
    @Transactional
    public void testLogin(){
        LoginParam loginParam=new LoginParam();
        loginParam.setName("user1").setPassword("123456");
        UserVo userVo=userService.login(loginParam);
        System.out.println(userVo);

        try{
            loginParam.setName("user33").setPassword("123456");
            userService.login(loginParam);
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            loginParam.setName("user1").setPassword("123457");
            userService.login(loginParam);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    public void testRegister(){
        String name="user33";
        String password="123456";
        String email="user33@163.com";
        String phone="10086";
        String city="北京";
        Long permissionId=1L;
        UserParam userParam=new UserParam();
        userParam.setName(name).setPassword(password).setEmail(email).setPhone(phone).setCity(city).setPermissionId(permissionId);
        userService.register(userParam);

        try{
            userParam.setName("user1");
            userService.register(userParam);
        }catch(Exception e){
            e.printStackTrace();
        }

        LoginParam loginParam=new LoginParam();
        loginParam.setName("user33").setPassword("123456");
        UserVo userVo=userService.login(loginParam);
        System.out.println(userVo);
    }

    @Test
    @Transactional
    public void testCRUD(){
        LambdaQueryWrapper<UserEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserEntity::getPermissionId, 14L)
            .eq(UserEntity::getName, "user14")
            .eq(UserEntity::getPassword, "123456");
        List<UserEntity>userEntityList=userService.list(lambdaQueryWrapper);
        for (UserEntity userEntity : userEntityList) {
            System.out.println(userEntity);
        }
    }
}
