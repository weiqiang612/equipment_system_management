package com.weiqiang.service.impl;

import com.weiqiang.dao.DepartmentDao;
import com.weiqiang.pojo.Department;
import com.weiqiang.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    // 查询所有单位信息
    @Override
    public List<Department> getDepts() {
        return departmentDao.getDepts();
    }

    @Override
    public int addDept(Department department) {
        return departmentDao.addDept(department);
    }

    // 删除单位
    @Override
    public int deleteDept(String unitCode) {
        return departmentDao.deleteDept(unitCode);
    }

    // 根据 unitCode 查询部门
    @Override
    public Department getDeptById(String unitCode) {
        return departmentDao.getDeptById(unitCode);
    }

    @Override
    public int updateDept(Department department,String unitCode) {
        return departmentDao.updateDept(department,unitCode);
    }
}
