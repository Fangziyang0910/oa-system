package com.whaler.oasys.model.vo;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * GroupVo 类定义了关于用户组的基本信息，包括组的ID、名称、描述以及关联的权限ID列表。
 * 该类使用了 Lombok 的 @Data 和 @Accessors 注解，自动生成了基本的 getter/setter 方法，
 * 并且设置了链式调用的访问器。
 * @param categoryId 用户组的唯一标识符
 * @param categoryName 用户组的名称
 * @param categoryDescription 用户组的描述信息
 * @param permissionIds 用户组关联的权限ID列表
 */
@Data
@Accessors(chain = true)
public class CategoryVo {
    private Long categoryId; // 用户组的唯一标识符
    private String categoryName; // 用户组的名称
    private String categoryDescription; // 用户组的描述信息
    private List<Long> permissionIds; // 用户组关联的权限ID列表
}
