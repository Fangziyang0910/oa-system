package com.whaler.oasys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.AdministratorEntity;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;

public interface AdministratorService
extends IService<AdministratorEntity> {
    AdministratorVo login(LoginParam loginParam);

    void register(AdministratorParam administratorParam);
}
