package com.whaler.oasys.controller.api;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.whaler.oasys.Main;
import com.whaler.oasys.model.param.StartFormParam;
import com.whaler.oasys.model.vo.ProcessInstanceVo;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.ApplicantService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@AutoConfigureMockMvc
@Rollback(true)
public class ApplicantControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ApplicantService applicantService;

    private static final ResultHandler resultHandler = new ResultHandler() {
        @Override
        public void handle(MvcResult result) throws Exception {
            result.getResponse().setCharacterEncoding("UTF-8");
            MockHttpServletResponse contentRespon = result.getResponse();
            ByteArrayInputStream bais = new ByteArrayInputStream(contentRespon.getContentAsByteArray());
            BufferedImage image = ImageIO.read(bais);
            File file=new File("./img/mvcGet.png");
            ImageIO.write(image, "PNG", file);
        }
    };

    private static final String token="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzk5ODIyNjQxLCJpYXQiOjE3MTM1MDkwNDF9.ttlfCi9hrQrTldggyN_pimihfnOvnFsJv9DyL59_9hwgVKUXfrWrs0T96pTtV5jZ8-2d7RiiRbFLUfEv_GCHbQ";

    @Before
    public void setup()throws Exception {
        log.info("---------------start---------------");
    }

    @After
    public void tearDown()throws Exception {
        log.info("---------------end---------------");
    }

    @Test
    @Transactional
    public void testListProcessInstances()throws Exception{
        ResultActions resultActions=mockMvc.perform(
            MockMvcRequestBuilders.get("/applicant/listProcessInstances")
                .header("Authorization", token)
        );
        doResultActions(resultActions);
    }

    @Test
    @Transactional
    public void testListProcessDefinitions()throws Exception{
        ResultActions resultActions=mockMvc.perform(
            MockMvcRequestBuilders.get("/applicant/listProcessDefinitions")
                .header("Authorization", token)
        );
        doResultActions(resultActions);
    }

    @Test
    @Transactional
    public void testCreateProcessInstance()throws Exception{
        ResultActions resultActions=mockMvc.perform(
            MockMvcRequestBuilders.get("/applicant/createProcessInstance/leaveProcess")
                .header("Authorization", token)
        );
        doResultActions(resultActions);
    }

    @Test
    @Transactional
    public void testGetStartForm()throws Exception{
        ResultActions resultActions=mockMvc.perform(
            MockMvcRequestBuilders.get("/applicant/getStartForm/leaveProcess")
                .header("Authorization", token)
        );
        doResultActions(resultActions);
    }

    @Test
    @Transactional
    public void testSubmitStartForm() throws Exception {
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("leaveProcess");
        
        StartFormParam userParam=new StartFormParam();
        userParam.setProcessInstanceId(processInstanceVo.getProcessInstanceId());
        Map<String,String> startForm=new HashMap<>();
        startForm.put("applicantDepartment","研发部");
        // startForm.put("leader","5");
        // startForm.put("manager","6");
        userParam.setForm(startForm);

        ResultActions resultActions=mockMvc.perform(
            MockMvcRequestBuilders.post("/applicant/submitStartForm")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(userParam))
        );
        doResultActions(resultActions);
    }


    @Test
    @Transactional
    public void testGetOriginalProcessDiagram()throws Exception{
        
        ResultActions resultActions=mockMvc.perform(
            MockMvcRequestBuilders.get("/applicant/getOriginalProcessDiagram/leaveProcess")
                .header("Authorization", token)
        );
        // doResultActions(resultActions);
        resultActions.andDo(resultHandler);
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

    private void doStarterTask() {
        UserContext.setCurrentUserId(1L);
        ProcessInstanceVo processInstanceVo= applicantService.createProcessInstance("leaveProcess");
        Map<String,String> startForm=new HashMap<>();
        startForm.put("leader","5");
        startForm.put("manager","6");
        applicantService.submitStartForm(processInstanceVo.getProcessInstanceId(), startForm);
    }
}
