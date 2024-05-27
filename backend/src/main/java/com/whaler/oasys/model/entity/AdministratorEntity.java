package com.whaler.oasys.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 管理员实体类
 * 此类继承自基础实体类BaseEntity，用于映射数据库中的administrator表
 * @param name 管理员姓名
 * @param password 管理员密码
 * @param email 管理员邮箱
 * @param phone 管理员电话
 */
@Data
@Accessors(chain = true)
@TableName("administrator")
public class AdministratorEntity
extends BaseEntity {
    private String name; // 管理员姓名
    private String password; // 管理员密码
    private String email; // 管理员邮箱
    private String phone; // 管理员电话
}
