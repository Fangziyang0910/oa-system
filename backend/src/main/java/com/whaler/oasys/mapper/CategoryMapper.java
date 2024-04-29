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
    int insertCategoryEntity(
        @Param(value = "name") String name,
        @Param(value = "description") String description
    );
    int deleteCategoryEntity(@Param(value = "id") Long id);

    CategoryEntity selectByCategoryName(
        @Param(value = "name") String name
    );

    List<Long> selectPermissionsByCategoryId(Long categoryId);

    List<Long> selectCategorysByPermissionId(Long permissionId);

    int insertMembership(
        @Param(value = "permissionId") Long permissionId,
        @Param(value = "categoryId") Long categoryId
    );

    int deleteMymbership(
        @Param(value = "permissionId") Long permissionId,
        @Param(value = "categoryId") Long categoryId
    );
}
