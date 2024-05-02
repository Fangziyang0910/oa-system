package com.whaler.oasys.service;

import java.io.InputStream;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.AdministratorEntity;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;
import com.whaler.oasys.model.vo.ProcessDefinitionVo;

public interface AdministratorService
extends IService<AdministratorEntity> {
    AdministratorVo login(LoginParam loginParam);

    void register(AdministratorParam administratorParam);

    List<ProcessDefinitionVo> listProcessDefinitions();

    void deployProcessDefinition(InputStream[] files, String[] fileNames);
}
