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

    /**
     * 插入一个新的管理员信息到数据库中.
     * 
     * @param name 管理员姓名，不能为空.
     * @param password 管理员密码，不能为空. 注意：应该在插入前进行加密处理.
     * @param email 管理员电子邮箱地址，可以为空，为空时不做插入.
     * @param phone 管理员电话号码，可以为空，为空时不做插入.
     * @return 返回插入操作影响的行数，通常为1表示插入成功，为0表示插入失败或未执行.
     */
    int insertAdministrator(
        @Param("name")String name,
        @Param("password")String password,
        @Param("email")String email,
        @Param("phone")String phone
    );
}
