package com.whaler.oasys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.AdministratorMapper;
import com.whaler.oasys.model.entity.AdministratorEntity;
import com.whaler.oasys.model.param.AdministratorParam;
import com.whaler.oasys.model.param.LoginParam;
import com.whaler.oasys.model.vo.AdministratorVo;
import com.whaler.oasys.security.JwtManager;
import com.whaler.oasys.service.AdministratorService;

@Service
public class AdministratorServiceImpl
extends ServiceImpl<AdministratorMapper,AdministratorEntity>
implements AdministratorService {
    @Autowired
    private JwtManager jwtManager;
    @Override
    public AdministratorVo login(LoginParam loginParam){
        AdministratorEntity administratorEntity=
        this.baseMapper.selectByName(loginParam.getName());
        if(administratorEntity==null){
            throw new ApiException("用户名不存在");
        }
        if(!administratorEntity.getPassword().equals(loginParam.getPassword())){
            throw new ApiException("密码错误");
        }
        String token=jwtManager.generate(administratorEntity.getId());
        AdministratorVo administratorVo=new AdministratorVo();
        administratorVo.setName(administratorEntity.getName())
            .setToken(token)
            .setEmail(administratorEntity.getEmail())
            .setPhone(administratorEntity.getPhone());
        return administratorVo;
    }

    @Override
    public void register(AdministratorParam administratorParam){
        AdministratorEntity administratorEntity=
        this.baseMapper.selectByName(administratorParam.getName());
        if (administratorEntity!=null) {
            throw new ApiException("用户名已存在");
        }
        // 密码加密
        String newpassword=administratorParam.getPassword();
        this.baseMapper.insertAdministrator(
            administratorParam.getName(),
            newpassword,
            administratorParam.getEmail(),
            administratorParam.getPhone()
        );
    }
}
