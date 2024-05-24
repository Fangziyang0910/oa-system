package com.whaler.oasys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whaler.oasys.model.entity.CategoryEntity;

@Repository
@Mapper
public interface CategoryMapper
extends BaseMapper<CategoryEntity> {
    
    /**
     * 插入一个新的分类实体到数据库。
     * 
     * @param name 分类的名称，不能为空。
     * @param description 分类的描述信息，可以为空。
     * @return 返回插入操作影响的行数，通常为1表示插入成功。
     */
    int insertCategoryEntity(
        @Param(value = "name") String name,
        @Param(value = "description") String description
    );

    /**
     * 删除特定分类实体
     * 
     * @param id 分类实体的唯一标识符
     * @return 影响的行数，通常为删除成功的行数
     */
    int deleteCategoryEntity(@Param(value = "id") Long id);

    /**
     * 根据类别名称查询类别实体。
     * 
     * @param name 类别的名称，用于查询特定的类别实体。
     * @return 返回匹配给定名称的类别实体。如果没有找到匹配的实体，则返回null。
     */
    CategoryEntity selectByCategoryName(
        @Param(value = "name") String name
    );

    /**
     * 根据类别ID选择权限列表。
     * 
     * @param categoryId 类别的ID，用于查询与此ID相关联的权限列表。
     * @return 返回一个包含相关权限ID的列表。如果找不到相关权限，返回空列表。
     */
    List<Long> selectPermissionsByCategoryId(Long categoryId);

    /**
     * 根据权限ID选择相应的类别列表。
     * 
     * @param permissionId 权限ID，用于查询与其相关的类别列表。
     * @return 返回一个包含相关类别ID的列表。
     */
    List<Long> selectCategorysByPermissionId(Long permissionId);

    /**
     * 插入一个权限-分类关系记录到数据库。
     * 
     * @param permissionId 权限ID，标识特定的权限。
     * @param categoryId 分类ID，标识特定的分类。
     * @return 返回插入操作影响的行数，通常为1表示插入成功。
     */
    int insertMembership(
        @Param(value = "permissionId") Long permissionId,
        @Param(value = "categoryId") Long categoryId
    );

    /**
     * 删除特定权限与类别之间的成员关系。
     * 
     * @param permissionId 权限ID，用于指定要删除的权限。
     * @param categoryId 类别ID，用于指定要删除的类别。
     * @return 返回删除操作影响的记录数。
     */
    int deleteMymbership(
        @Param(value = "permissionId") Long permissionId,
        @Param(value = "categoryId") Long categoryId
    );
}
