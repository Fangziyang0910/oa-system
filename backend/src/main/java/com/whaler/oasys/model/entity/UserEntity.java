package com.whaler.oasys.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户实体类，继承自基础实体类BaseEntity。
 * 使用了Lombok注解来简化实体类的代码编写。
 * 
 * @param name 用户名
 * @param password 密码
 * @param email 电子邮件
 * @param phone 电话号码
 * @param city 所在城市
 * @param permissionId 权限标识符
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("user")
public class UserEntity
extends BaseEntity {
    private String name; // 用户名
    private String password; // 密码
    private String email; // 电子邮件
    private String phone; // 电话号码
    private String city; // 所在城市
    private Long permissionId; // 权限标识符
}
