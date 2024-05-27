package com.whaler.oasys.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whaler.oasys.mapper.CategoryMapper;
import com.whaler.oasys.model.entity.CategoryEntity;
import com.whaler.oasys.model.vo.CategoryVo;
import com.whaler.oasys.service.CategoryService;

@Service
public class CategoryServiceImpl
extends ServiceImpl<CategoryMapper,CategoryEntity>
implements CategoryService {
    /**
     * 根据分类ID查询分类信息及其权限列表。
     * 
     * @param categoryId 分类的唯一标识符。
     * @return CategoryVo 分类视图对象，包含了分类的详细信息和权限ID列表。
     * @throws ApiException 如果指定ID的分类不存在，则抛出异常。
     */
    @Override
    public CategoryVo selectByCategoryId(Long categoryId) {
        // 通过分类ID查询分类实体
        CategoryEntity categoryEntity=this.baseMapper.selectById(categoryId);
        if(categoryEntity==null){
            // 如果找不到对应的分类实体，抛出异常
            throw new ApiException("未找到该组");
        }
        // 根据分类ID查询权限ID列表
        List<Long>permissions=this.baseMapper.selectPermissionsByCategoryId(categoryId);
        // 创建分类视图对象，并设置相关属性
        CategoryVo categoryVo=new CategoryVo();
        categoryVo.setCategoryId(categoryId)
            .setCategoryName(categoryEntity.getName())
            .setCategoryDescription(categoryEntity.getDescription())
            .setPermissionIds(permissions);
        return categoryVo;
    }

    /**
     * 根据分类名称选择分类信息。
     * 
     * @param categoryName 分类的名称，用于查询特定的分类信息。
     * @return CategoryVo 返回分类的视图对象，包含分类的详细信息和权限ID列表。
     * @throws ApiException 如果查询不到对应的分类信息，则抛出异常。
     */
    @Override
    public CategoryVo selectByCategoryName(String categoryName) {
        // 通过分类名称查询分类实体
        CategoryEntity categoryEntity=this.baseMapper.selectByCategoryName(categoryName);
        if(categoryEntity==null){
            // 如果找不到对应的分类实体，抛出异常
            throw new ApiException("未找到该组");
        }
        // 根据分类ID查询权限列表
        List<Long>permissions=this.baseMapper.selectPermissionsByCategoryId(categoryEntity.getId());
        // 创建分类视图对象，并设置相关属性
        CategoryVo categoryVo=new CategoryVo();
        categoryVo.setCategoryId(categoryEntity.getId())
            .setCategoryName(categoryName)
            .setCategoryDescription(categoryEntity.getDescription())
            .setPermissionIds(permissions);
        return categoryVo;
    }

    /**
     * 插入一个权限与分类的成员关系。
     * <p>此方法通过调用基础映射器（baseMapper）来插入一个特定权限和分类之间的成员关系记录。</p>
     * 
     * @param permissionId 权限的ID，标识特定的权限。
     * @param categoryId 分类的ID，标识特定的分类。
     * @see #baseMapper 用于执行实际数据库操作的基础映射器接口。
     */
    @Override
    public void insertMembership(Long permissionId, Long categoryId) {
        this.baseMapper.insertMembership(permissionId, categoryId);
    }

    /**
     * 删除特定权限和类别之间的成员关系。
     * 
     * @param permissionId 权限的ID，用于标识特定的权限。
     * @param categoryId 类别的ID，用于标识特定的类别。
     * 这个方法通过调用baseMapper的deleteMymbership方法，来删除指定权限ID和类别ID之间的成员关系。
     * 不存在返回值，操作完成后不返回任何结果。
     */
    @Override
    public void removeMymbership(Long permissionId, Long categoryId) {
        this.baseMapper.deleteMymbership(permissionId, categoryId);
    }

    /**
     * 根据权限ID选择对应的分类ID列表。
     * 
     * @param permissionId 权限的唯一标识符，用于查询与其相关的分类ID。
     * @return 返回一个包含相关分类ID的列表。如果没有任何分类与该权限相关，则返回空列表。
     */
    @Override
    public List<Long> selectCategoryIdsByPermissionId(Long permissionId) {
        // 通过权限ID查询相关的分类信息
        return this.baseMapper.selectCategorysByPermissionId(permissionId);
    }
}
