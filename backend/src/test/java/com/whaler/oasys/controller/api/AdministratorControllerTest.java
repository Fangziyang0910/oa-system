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
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc
@Rollback(true)
public class AdministratorControllerTest {
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
        LoginParam loginParam=new LoginParam();
        loginParam.setName("admin1").setPassword("123456");
        ResultActions resultActions=
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/admin/login")
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
        AdministratorParam administratorParam=new AdministratorParam();
        administratorParam.setName("admin2")
            .setPassword("123456")
            .setEmail("admin2@163.com")
            .setPhone("10086");
        ResultActions resultActions=
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(administratorParam)
            )
        );
        doResultActions(resultActions);
        System.out.println("---------------testRegister---------------");
    }

    @Test
    @Transactional
    public void testValidateToken()throws Exception {
        System.out.println("---------------testValidateToken---------------");
        ResultActions resultActions=
        mockMvc.perform(
            MockMvcRequestBuilders.get("/admin/validateToken")
                .header("Authorization", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxODAxMzE1ODgzLCJpYXQiOjE3MTUwMDIyODN9.vE59AqHjYEVh92ZPTLj4aQE9xunop_EmGoOD_jk7jbHrsaAV3_9u1UaA_IS_BoRrMrvgzZOXKG-ehr9XPI6vnQ")
        );
        doResultActions(resultActions);
        System.out.println("---------------testValidateToken---------------");
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
