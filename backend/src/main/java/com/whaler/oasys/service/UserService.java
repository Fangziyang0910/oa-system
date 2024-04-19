package com.whaler.oasys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.param.UserParam;
import com.whaler.oasys.model.vo.UserVo;

public interface UserService
extends IService<UserEntity> {
    UserVo login(LoginParam loginParam);

    void register(UserParam userParam);

    
}
