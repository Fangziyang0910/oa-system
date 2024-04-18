package com.whaler.oasys.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("administrator")
public class AdministratorEntity
extends BaseEntity {
    private String name;
    private String password;
    private String email;
    private String phone;
}
