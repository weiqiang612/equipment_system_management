package com.weiqiang.service.impl;

import com.weiqiang.dao.CategoryDao;
import com.weiqiang.pojo.Category;
import com.weiqiang.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public List<Category> getCategories() {
        return categoryDao.getCategories();
    }

    @Override
    public Category getCategoryById(String categoryId) {
        return categoryDao.getCategoryById(categoryId);
    }

    @Override
    public int addCategory(Category category) {
        return categoryDao.addCategory(category);
    }

    @Override
    public int deleteCategoryById(String categoryId) {
        return categoryDao.deleteCategoryById(categoryId);
    }

    @Override
    public int updateCategory(Category category,String categoryId) {
        return categoryDao.updateCategory(category,categoryId);
    }
}
