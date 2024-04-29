package com.whaler.oasys.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * GroupEntity 类表示一个群组实体，它继承了 BaseEntity 类，并且映射到了数据库中的 "group" 表。
 * 这个类使用了 Lombok 的注解来简化对象的创建和数据的访问。
 * @param name 分组名称
 * @param description 分组的描述信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("category")
public class CategoryEntity
extends BaseEntity {
    private String name; // 群组的名称
    private String description; // 群组的描述信息
}
