package com.whaler.oasys.controller.api;

import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.whaler.oasys.Main;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.service.AdministratorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc
@Rollback(true)
public class AdministratorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AdministratorService administratorService;

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

    @Test
    @Transactional
    public void testDeployProcess() throws Exception{
        System.out.println("---------------testDeployProcess---------------");
        InputStream file= this.getClass().getClassLoader().getResourceAsStream("upload/LeaveProcess1.bpmn20.xml");
        InputStream form= this.getClass().getClassLoader().getResourceAsStream("upload/LeaveAsk.form");
        String fileName="LeaveProcess1.bpmn20.xml";
        String formName="LeaveAsk.form";
        MockMultipartFile multipartFile=new MockMultipartFile("files", fileName, "multipart/form-data",file);
        MockMultipartFile formFile=new MockMultipartFile("files", formName, "multipart/form-data",form);
        ResultActions resultActions=
            mockMvc.perform(
                MockMvcRequestBuilders.multipart("/admin/deployProcess")
                .file(multipartFile)
                .file(formFile)
        );
        doResultActions(resultActions);

        List<ProcessDefinitionVo> processDefinitionVos = administratorService.listProcessDefinitions();
        log.info("processDefinitionVos:{}",processDefinitionVos);
        System.out.println("---------------testDeployProcess---------------");
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
