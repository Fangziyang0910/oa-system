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
    @Override
    public CategoryVo selectByCategoryId(Long categoryId) {
        CategoryEntity categoryEntity=this.baseMapper.selectById(categoryId);
        if(categoryEntity==null){
            throw new ApiException("未找到该组");
        }
        List<Long>permissions=this.baseMapper.selectPermissionsByCategoryId(categoryId);
        CategoryVo categoryVo=new CategoryVo();
        categoryVo.setCategoryId(categoryId)
            .setCategoryName(categoryEntity.getName())
            .setCategoryDescription(categoryEntity.getDescription())
            .setPermissionIds(permissions);
        return categoryVo;
    }

    @Override
    public CategoryVo selectByCategoryName(String categoryName) {
        CategoryEntity categoryEntity=this.baseMapper.selectByCategoryName(categoryName);
        if(categoryEntity==null){
            throw new ApiException("未找到该组");
        }
        List<Long>permissions=this.baseMapper.selectPermissionsByCategoryId(categoryEntity.getId());
        CategoryVo categoryVo=new CategoryVo();
        categoryVo.setCategoryId(categoryEntity.getId())
            .setCategoryName(categoryName)
            .setCategoryDescription(categoryEntity.getDescription())
            .setPermissionIds(permissions);
        return categoryVo;
    }

    @Override
    public void insertMembership(Long permissionId, Long categoryId) {
        this.baseMapper.insertMembership(permissionId, categoryId);
    }

    @Override
    public void removeMymbership(Long permissionId, Long categoryId) {
        this.baseMapper.deleteMymbership(permissionId, categoryId);
    }

    @Override
    public List<Long> selectCategoryIdsByPermissionId(Long permissionId) {
        return this.baseMapper.selectCategorysByPermissionId(permissionId);
    }
}
