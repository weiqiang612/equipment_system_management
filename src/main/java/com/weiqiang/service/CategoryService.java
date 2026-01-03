package com.weiqiang.service;

import com.weiqiang.pojo.Category;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */


public interface CategoryService {
    // 查询所有分类
    List<Category> getCategories();

    // 根据ID查询分类
    Category getCategoryById(String categoryId);

    // 添加分类
    int addCategory(Category category);

    // 删除分类
    int deleteCategoryById(String categoryId);

    // 修改分类
    int updateCategory(Category category,String categoryId);
}
