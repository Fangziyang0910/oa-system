package com.whaler.oasys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.param.UserParam;
import com.whaler.oasys.model.vo.UserVo;

/**
 * 用户服务接口，提供用户管理相关操作。
 * 继承自IService<UserEntity>，扩展对用户实体的操作。
 */
public interface UserService
extends IService<UserEntity> {
    
    /**
     * 用户登录。
     * 
     * @param loginParam 登录参数，包含用户名和密码。
     * @return 登录成功返回用户信息，失败返回null或特定错误信息。
     */
    UserVo login(LoginParam loginParam);

    /**
     * 用户注册。
     * 
     * @param userParam 注册参数，包含用户基本信息。
     * @void 方法，注册成功或失败通过其他方式（如异常）通知调用方。
     */
    void register(UserParam userParam);

    /**
     * 根据用户ID查询用户信息。
     * 
     * @param userId 用户ID，用于查询特定用户信息。
     * @return 返回对应用户ID的用户信息，如果不存在则返回null。
     */
    UserVo selectByUserId(Long userId);
}
