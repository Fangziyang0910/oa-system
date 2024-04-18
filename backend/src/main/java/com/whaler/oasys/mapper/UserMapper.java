package com.whaler.oasys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.model.entity.UserPermissionEntity;

@Repository
@Mapper
public interface UserMapper
extends BaseMapper<UserEntity> {
    /**
     * 根据用户实体获取用户详细信息。
     * 
     * @param name 用户名，用于查询用户详细信息。
     * @return 返回匹配的用户实体，如果找不到则返回null。
     */
    UserEntity selectByName(String name);

    /**
     * 插入一个新的用户实体到数据库。
     * 
     * @param name 用户名
     * @param password 密码。注意：应该在插入前进行加密处理。
     * @param email 用户的电子邮箱地址
     * @param phone 用户的电话号码
     * @param city 用户所在城市
     * @param permissionId 用户拥有的权限ID，表示用户的角色或权限等级。
     * 
     * @return int 插入操作影响的行数
     */
    int insertUserEntity(
        @Param("name") String name,
        @Param("password") String password,
        @Param("email") String email,
        @Param("phone") String phone,
        @Param("city") String city,
        @Param("permissionId") Long permissionId
    );

    /**
     * 根据名称删除某个实体，并返回删除的实体数量。
     * 
     * @param name 要删除的实体的名称。
     * @return 返回删除的实体数量。如果删除了1个或多个实体，返回它们的数量；如果没有找到匹配的实体，则返回0。
     */
    int deleteByName(String name);

    UserPermissionEntity selecUserPermissionEntityByName(String name);

}
