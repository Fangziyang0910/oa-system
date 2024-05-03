package com.whaler.oasys.controller.api;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;
import com.whaler.oasys.service.AdministratorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/admin")
@Api(description = "系统管理员")
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;

    @ApiOperation("系统管理员登录")
    @PostMapping("/login")
    public AdministratorVo login(@RequestBody @Validated LoginParam loginParam) {
        return administratorService.login(loginParam);
    }

    @ApiOperation("系统管理员注册")
    @PostMapping("/register")
    public void register(@RequestBody @Validated AdministratorParam AdministratorParam) {
        administratorService.register(AdministratorParam);
    }

    @ApiOperation("系统管理员查询所有的流程定义")
    @GetMapping("/listProcessDefinitions")
    public List<ProcessDefinitionVo> listProcessDefinitions() {
        return administratorService.listProcessDefinitions();
    }

    @ApiOperation("系统管理员部署流程定义")
    @PostMapping("/deployProcessDefinition")
    public void deployProcessDefinition(
        MultipartFile[] multipartFiles
    ) {
        InputStream[] files = new InputStream[multipartFiles.length];
        String[] filenames = new String[multipartFiles.length];
        try{
            for(int i=0;i<multipartFiles.length;i++){
                files[i] = multipartFiles[i].getInputStream();
                filenames[i] = multipartFiles[i].getOriginalFilename();
            }
        }catch(Exception e){
            throw new ApiException(e.getMessage());
        }
        administratorService.deployProcessDefinition(files, filenames);
    }
}
