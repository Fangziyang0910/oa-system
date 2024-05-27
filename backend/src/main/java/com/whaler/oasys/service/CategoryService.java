package com.whaler.oasys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.CategoryEntity;
import com.whaler.oasys.model.vo.CategoryVo;

/**
 * 类别服务接口，提供关于类别管理的基本操作方法。
 * 继承自IService<CategoryEntity>，针对CategoryEntity实体提供服务。
 */
public interface CategoryService
extends IService<CategoryEntity> {
    
    /**
     * 根据类别ID查询类别信息。
     * 
     * @param categoryId 类别的ID，不能为空。
     * @return 返回对应的CategoryVo对象，包含类别的详细信息。
     */
    CategoryVo selectByCategoryId(Long categoryId);

    /**
     * 根据类别名称查询类别信息。
     * 
     * @param categoryName 类别的名称，不能为空。
     * @return 返回对应的CategoryVo对象，包含类别的详细信息。
     */
    CategoryVo selectByCategoryName(String categoryName);
    
    /**
     * 增加权限与类别的关联关系。
     * 
     * @param permissionId 权限的ID，不能为空。
     * @param categoryId 类别的ID，不能为空。
     */
    void insertMembership(Long permissionId, Long categoryId);

    /**
     * 移除权限与类别的关联关系。
     * 
     * @param permissionId 权限的ID，不能为空。
     * @param categoryId 类别的ID，不能为空。
     */
    void removeMymbership(Long permissionId, Long categoryId);

    /**
     * 根据权限ID查询关联的类别ID列表。
     * 
     * @param permissionId 权限的ID，不能为空。
     * @return 返回类别ID的列表。
     */
    List<Long> selectCategoryIdsByPermissionId(Long permissionId);
}
