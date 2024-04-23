package com.whaler.oasys.controller.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.whaler.oasys.Main;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.param.UserParam;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc
@Rollback(true)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Before
    public void setup()throws Exception {
        System.out.println("---------------start---------------");
    }

    @After
    public void tearDown()throws Exception {
        System.out.println("---------------end---------------");
    }

    @Test
    @Transactional
    public void testLogin()throws Exception {
        System.out.println("---------------testLogin---------------");
        LoginParam loginParam = new LoginParam();
        loginParam.setName("user1").setPassword("123456");
        ResultActions resultActions=
        mockMvc.perform(
            MockMvcRequestBuilders.post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(loginParam))
        );
        doResultActions(resultActions);
        System.out.println("---------------testLogin---------------");
    }

    @Test
    @Transactional
    public void testRegister()throws Exception {
        System.out.println("---------------testRegister---------------");
        UserParam userParam = new UserParam();
        userParam.setName("user33")
            .setPassword("123456")
            .setEmail("user33@163.com")
            .setPhone("10086")
            .setCity("北京")
            .setPermissionId(1L);
        ResultActions resultActions=
        mockMvc.perform(
            MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(userParam))
        );
       doResultActions(resultActions);
       System.out.println("---------------testRegister---------------");
    }

    private void doResultActions(ResultActions resultActions)throws Exception{
        resultActions.andReturn()
            .getResponse()
            .setCharacterEncoding("UTF-8");
        resultActions.andExpect(
            MockMvcResultMatchers.status().isOk()
        )
            .andDo(MockMvcResultHandlers.print());
    }
}
