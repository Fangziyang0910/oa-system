package com.whaler.oasys.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.whaler.oasys.model.entity.CategoryEntity;

@RunWith(SpringRunner.class)
@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(true)
public class CategoryMapperTest {
    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    @Transactional
    public void testInsertCategoryEntity() {
        categoryMapper.insertCategoryEntity("test", "test");
    }

    @Test
    @Transactional
    public void testDeleteCategoryEntity() {
        categoryMapper.deleteCategoryEntity(1L);
    }

    @Test
    @Transactional
    public void testSelectByCategoryName() {
        CategoryEntity categoryEntity = categoryMapper.selectByCategoryName("运维部");
        System.out.println(categoryEntity);
    }

    @Test
    @Transactional
    public void testSelectPermissionsByCategoryId() {
        List<Long> permissions = categoryMapper.selectPermissionsByCategoryId(1L);
        for (Long permission : permissions) {
            System.out.println(permission);
        }
    }

    @Test
    @Transactional
    public void testSelectCategorysByPermissionId() {
        List<Long> categorys = categoryMapper.selectCategorysByPermissionId(1L);
        for (Long category : categorys) {
            System.out.println(category);
        }
    }

    @Test
    @Transactional
    public void testInsertMembership() {
        categoryMapper.insertMembership(55L, 1L);
        List<Long>permissions=categoryMapper.selectPermissionsByCategoryId(1L);
        for (Long permission : permissions) {
            System.out.println(permission);
        }
    }

    @Test
    @Transactional
    public void testDeleteMymbership() {
        categoryMapper.deleteMymbership(1L, 1L);
        List<Long>permissions=categoryMapper.selectPermissionsByCategoryId(1L);
        for (Long permission : permissions) {
            System.out.println(permission);
        }
    }
}
