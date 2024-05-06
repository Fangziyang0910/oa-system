package com.whaler.oasys.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.whaler.oasys.Main;
import com.whaler.oasys.model.vo.CategoryVo;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
@Rollback(true)
@Slf4j
public class CategoryServiceTest {
    @Autowired
    private CategoryService categoryService;

    @Test
    @Transactional
    public void testSelectByCategoryId() {
        CategoryVo categoryVo = categoryService.selectByCategoryId(1L);
        log.info("categoryVo: {}", categoryVo);
    }

    @Test
    @Transactional
    public void testSelectByCategoryName() {
        CategoryVo categoryVo = categoryService.selectByCategoryName("研发部");
        log.info("categoryVo: {}", categoryVo);
    }

    @Test
    @Transactional
    public void testInsertMembership() {
        categoryService.insertMembership(1L, 2L);
        CategoryVo categoryVo = categoryService.selectByCategoryId(2L);
        log.info("categoryVo: {}", categoryVo);
    }

    @Test
    @Transactional
    public void testRemoveMymbership() {
        categoryService.removeMymbership(1L, 1L);
        CategoryVo categoryVo = categoryService.selectByCategoryId(1L);
        log.info("categoryVo: {}", categoryVo);
    }

    @Test
    @Transactional
    public void testSelectCategoryIdsByPermissionId() {
        List<Long> categoryIds = categoryService.selectCategoryIdsByPermissionId(5L);
        log.info("categoryIds: {}", categoryIds);
    }
}
