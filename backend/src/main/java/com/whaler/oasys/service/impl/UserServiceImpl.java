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
import com.whaler.oasys.service.UserService;

@Service
@Transactional
public class UserServiceImpl
extends ServiceImpl<UserMapper,UserEntity>
implements UserService {
    @Autowired
    private JwtManager jwtManager;
    /**
     * 用户登录接口。
     * 
     * @param loginParam 包含登录名称和密码的参数对象。
     * @return UserVo 登录成功后返回的用户信息Vo对象，包含用户基本信息和token。
     * @throws ApiException 如果用户名不存在或密码不正确，则抛出异常。
     */
    @Override
    public UserVo login(LoginParam loginParam) {
        // 根据用户名查询用户实体
        UserEntity userEntity=this.baseMapper.selectByName(loginParam.getName());
        if(userEntity==null){
            // 用户名不存在时的异常处理
            throw new ApiException("用户名或密码错误");
        }
        // 验证密码
        if(!userEntity.getPassword().equals(loginParam.getPassword())){
            // 密码不匹配时的异常处理
            throw new ApiException("用户名或密码错误");
        }
        // 生成并设置JWT Token
        String token=jwtManager.generate(userEntity.getId());

        // 查询用户权限信息
        UserPermissionEntity userPermissionEntity=this.baseMapper.selecUserPermissionEntityByName(userEntity.getName());

        // 组装用户信息Vo并返回
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

    /**
     * 注册用户。
     * 
     * @param userParam 用户参数对象，包含用户名、密码、电子邮件、电话和城市等信息。
     * @throws ApiException 如果用户名已存在，则抛出此异常。
     */
    @Override
    public void register(UserParam userParam) {
        // 检查用户是否已存在
        UserEntity existedUser=this.baseMapper.selectByName(userParam.getName());
        if(existedUser!=null){
            // 抛出异常，提示用户名已存在
            throw new ApiException("用户名已存在");
        }
        
        // 对密码进行加密处理
        String newpassword=userParam.getPassword();
        
        // 插入新用户到数据库
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
