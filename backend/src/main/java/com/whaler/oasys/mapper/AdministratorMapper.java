package com.whaler.oasys.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whaler.oasys.model.entity.AdministratorEntity;

@Repository
@Mapper
public interface AdministratorMapper
extends BaseMapper<AdministratorEntity> {
    AdministratorEntity selectByName(String name);

    int insertAdministrator(
        @Param("name")String name,
        @Param("password")String password,
        @Param("email")String email,
        @Param("phone")String phone
    );
}
