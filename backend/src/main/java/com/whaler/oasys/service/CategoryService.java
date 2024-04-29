package com.whaler.oasys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whaler.oasys.model.entity.CategoryEntity;
import com.whaler.oasys.model.vo.CategoryVo;

public interface CategoryService
extends IService<CategoryEntity> {
    CategoryVo selectByCategoryId(Long categoryId);

    CategoryVo selectByCategoryName(String categoryName);
    
    void insertMembership(Long permissionId, Long categoryId);

    void removeMymbership(Long permissionId, Long categoryId);

    List<Long> selectCategoryIdsByPermissionId(Long permissionId);
}
