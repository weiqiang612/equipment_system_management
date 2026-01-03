package com.weiqiang.controller;

import com.weiqiang.pojo.Category;
import com.weiqiang.pojo.Department;
import com.weiqiang.pojo.Result;
import com.weiqiang.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 获取所有分类
    @GetMapping
    public Result getCategories(){
        List<Category> categories = categoryService.getCategories();
        log.info("进行了查询分类的操作，结果数量为：{}",categories.size());
        return Result.success(categories);
    }

    // 根据categoryId查询部门
    @GetMapping("/{categoryId}")
    public Result getDeptsById(@PathVariable("categoryId") String categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        log.info("进行了查询部门的操作，结果为：" + category);
        return category != null ? Result.success(category) : Result.error("未查询到该部门");
    }

    // 增加分类
    @PostMapping
    public Result addCategory(@RequestBody Category category){
        int i = categoryService.addCategory(category);
        return i > 0 ? Result.success() : Result.error("添加部门失败!");
    }

    // 删除分类
    @DeleteMapping("/{categoryId}")
    public Result deleteCategory(@PathVariable("categoryId") String categoryId){
        int i = categoryService.deleteCategoryById(categoryId);
        return i > 0 ? Result.success() : Result.error("删除失败!");
    }

    // 修改分类
    @PutMapping("/{categoryId}")
    public Result updateCategory(@RequestBody Category category,@PathVariable("categoryId") String categoryId){
        int i = categoryService.updateCategory(category,categoryId);
        return i > 0 ? Result.success() : Result.error("修改部门失败!");
    }


}
