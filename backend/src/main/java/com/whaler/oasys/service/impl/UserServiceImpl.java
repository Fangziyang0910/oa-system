package com.whaler.oasys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.UserMapper;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.model.entity.UserPermissionEntity;
import com.whaler.oasys.model.exception.ApiException;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.param.UserParam;
import com.whaler.oasys.model.vo.UserVo;
import com.whaler.oasys.security.JwtManager;
import com.whaler.oasys.security.UserContext;
import com.whaler.oasys.service.UserService;

@Service
@Transactional
public class UserServiceImpl
extends ServiceImpl<UserMapper,UserEntity>
implements UserService {
    @Autowired
    private JwtManager jwtManager;
    @Override
    public UserVo login(LoginParam loginParam) {
        UserEntity userEntity=this.baseMapper.selectByName(loginParam.getName());
        if(userEntity==null){
            // 异常处理
            throw new ApiException("用户名或密码错误");
        }
        if(!userEntity.getPassword().equals(loginParam.getPassword())){
            // 异常处理
            throw new ApiException("用户名或密码错误");
        }
        // 创建 jwt token
        String token=jwtManager.generate(userEntity.getId());
        UserPermissionEntity userPermissionEntity=this.baseMapper.selecUserPermissionEntityByName(userEntity.getName());

        UserVo userVo=new UserVo();
        userVo.setUserId(userEntity.getId())
            .setName(userEntity.getName())
            .setToken(token)
            .setEmail(userEntity.getEmail())
            .setPhone(userEntity.getPhone())
            .setCity(userEntity.getCity())
            .setPermissionId(userEntity.getPermissionId())
            .setDepartment(userPermissionEntity.getDepartment())
            .setRole(userPermissionEntity.getRole())
            .setIsApplicant(userPermissionEntity.getIsApplicant())
            .setIsApprover(userPermissionEntity.getIsApprover())
            .setIsOperator(userPermissionEntity.getIsOperator());
        return userVo;
    }

    @Override
    public void register(UserParam userParam) {
        UserEntity existedUser=this.baseMapper.selectByName(userParam.getName());
        if(existedUser!=null){
            // 异常处理
            throw new ApiException("用户名已存在");
        }
        // 密码加密
        String newpassword=userParam.getPassword();
        this.baseMapper.insertUserEntity(
            userParam.getName(),
            newpassword,
            userParam.getEmail(),
            userParam.getPhone(),
            userParam.getCity(),
            userParam.getPermissionId()
        );
        return;
    }

    @Override
    public UserVo selectByUserId(Long userId) {
        UserEntity userEntity=this.baseMapper.selectById(userId);
        UserPermissionEntity userPermissionEntity=this.baseMapper.selecUserPermissionEntityByName(userEntity.getName());

        UserVo userVo=new UserVo();
        userVo.setUserId(userEntity.getId())
            .setName(userEntity.getName())
            .setToken(null)
            .setEmail(userEntity.getEmail())
            .setPhone(userEntity.getPhone())
            .setCity(userEntity.getCity())
            .setPermissionId(userEntity.getPermissionId())
            .setDepartment(userPermissionEntity.getDepartment())
            .setRole(userPermissionEntity.getRole())
            .setIsApplicant(userPermissionEntity.getIsApplicant())
            .setIsApprover(userPermissionEntity.getIsApprover())
            .setIsOperator(userPermissionEntity.getIsOperator());
        return userVo;

    }
}
