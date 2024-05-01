package com.whaler.oasys.service;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.whaler.oasys.Main;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
public class AdministratorServiceTest {
    @Autowired
    private AdministratorService administratorService;

    @Test
    @Transactional
    public void testLogin() {
        LoginParam loginParam = new LoginParam();
        loginParam.setName("admin1");
        loginParam.setPassword("123456");
        AdministratorVo administratorVo = administratorService.login(loginParam);

        System.out.println(administratorVo);
    }

    @Test
    @Transactional
    public void testRegister() {
        AdministratorParam administratorParam = new AdministratorParam();
        administratorParam.setName("admin2");
        administratorParam.setPassword("123456");
        administratorParam.setEmail("admin2@mail");
        administratorParam.setPhone("10086");

        administratorService.register(administratorParam);

        try {
            administratorParam.setName("admin1");
            administratorService.register(administratorParam);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        LoginParam loginParam = new LoginParam();
        loginParam.setName("admin2");
        loginParam.setPassword("123456");
        AdministratorVo administratorVo = administratorService.login(loginParam);
        System.out.println(administratorVo);
    }

    @Test
    @Transactional
    public void testListProcessDefinitions() {
        List<ProcessDefinitionVo> processDefinitionVos = administratorService.listProcessDefinitions();
        log.info("processDefinitionVos:{}",processDefinitionVos);
    }

    @Test
    @Transactional
    public void testDeployProcessDefinition() {
        InputStream file= this.getClass().getClassLoader().getResourceAsStream("upload/LeaveProcess1.bpmn20.xml");
        InputStream[] files= new InputStream[]{file};
        String[] fileNames= new String[]{"LeaveProcess1.bpmn20.xml"};
        administratorService.deployProcessDefinition(files,fileNames);
        
        List<ProcessDefinitionVo> processDefinitionVos = administratorService.listProcessDefinitions();
        log.info("processDefinitionVos:{}",processDefinitionVos);
    }
}
