package com.weiqiang.controller;

import com.weiqiang.pojo.Department;
import com.weiqiang.pojo.Result;
import com.weiqiang.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 * 对部门增删改查操作
 */

@Slf4j
@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // 查看部门信息
    @GetMapping
    public Result getDepts() {
        List<Department> departments = departmentService.getDepts();
        log.info("进行了查询所有部门的操作，结果数量为：{}", departments.size());
        return Result.success(departments);
    }

    // 根据unitCode查询部门
    @GetMapping("/{unitCode}")
    public Result getDeptsById(@PathVariable("unitCode") String unitCode) {
        Department department = departmentService.getDeptById(unitCode);
        log.info("进行了查询部门的操作，结果为：" + department);
        return department != null ? Result.success(department) : Result.error("未查询到该部门");
    }


    // 增加部门
    @PostMapping
    public Result addDept(@RequestBody Department department) {
        int i = departmentService.addDept(department);
        return i > 0 ? Result.success() : Result.error("插入单位失败!");
    }

    // 根据ID更新部门
    @PutMapping("/{unitCode}")
    public Result updateDept(@RequestBody Department department,
                             @PathVariable("unitCode") String unitCode) {
        int i = departmentService.updateDept(department,unitCode);
        return i > 0 ? Result.success() : Result.error("更新单位失败!");
    }
    // 删除部门
    @DeleteMapping("/{unitCode}")
    public Result deleteDept(@PathVariable("unitCode") String unitCode) {
        int i = departmentService.deleteDept(unitCode);
        return i > 0 ? Result.success() : Result.error("删除单位失败!");
    }

}
