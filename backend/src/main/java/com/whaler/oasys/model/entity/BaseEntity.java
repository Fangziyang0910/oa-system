package com.whaler.oasys.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * 实体基类，提供了一些基本的实体属性和方法。
 * 这是一个抽象类，用于定义所有实体对象共有的属性和行为。
 * 其中，id属性作为主键标识实体对象，采用自动增长的方式。
 * 
 * @param id 主键id
 */
@Data
public abstract class BaseEntity {
    /**
     * 主键id，采用自动增长策略。
     * @TableId 注解用于指定主键字段，type属性指明主键生成策略为自动增长。
     */
    @TableId(type = IdType.AUTO)
    private Long id;
}
