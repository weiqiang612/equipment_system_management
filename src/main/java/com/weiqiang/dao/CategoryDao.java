package com.weiqiang.dao;

import com.weiqiang.pojo.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Repository
public class CategoryDao extends BasicDao<Category>{
    public List<Category> getCategories() {
        String sql = "SELECT category_id categoryId , category_name categoryName , useful_life usefulLife ,residual_rate residualRate FROM category";
        return mutiSelect(sql, Category.class, null);
    }

    public Category getCategoryById(String categoryId) {
        String sql = "SELECT category_id categoryId , category_name categoryName , " +
                "useful_life usefulLife , residual_rate residualRate FROM category where category_id = ?";
        return selectOne(sql,Category.class,categoryId);
    }

    public int addCategory(Category category) {
        String sql = "INSERT INTO category VALUES(?,?,?,?)";
        return update(sql,
                category.getCategoryId(),category.getCategoryName(),category.getUsefulLife(),category.getResidualRate());

    }

    public int deleteCategoryById(String categoryId) {
        String sql = "DELETE FROM category WHERE category_id = ?";
        return update(sql,categoryId);
    }

    public int updateCategory(Category category,String categoryId) {
        String sql = "UPDATE category SET category_name = ? , useful_life = ?, residual_rate = ? WHERE category_id = ?";
        return update(sql,category.getCategoryName(),category.getUsefulLife(),category.getResidualRate(),categoryId);
    }
}
