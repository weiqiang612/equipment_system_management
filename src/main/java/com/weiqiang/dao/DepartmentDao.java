package com.weiqiang.dao;

import com.weiqiang.pojo.Department;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Repository
public class DepartmentDao extends BasicDao<Department> {
    public List<Department> getDepts() {
        String sql = "SELECT unit_code unitCode , unit_name unitName , manager FROM department";
        return mutiSelect(sql, Department.class, null);
    }

    public int addDept(Department department) {
        String sql = "INSERT INTO department VALUES (?,?,?)";
        return update(sql,
                department.getUnitCode(),department.getUnitName(),department.getManager());
    }

    public int deleteDept(String unitCode) {
        String sql = "DELETE FROM department WHERE unit_code = ?";
        return update(sql,unitCode);
    }

    public Department getDeptById(String unitCode) {
        String sql = "SELECT unit_code unitCode , unit_name unitName , manager FROM department where unit_code = ?";
        return selectOne(sql, Department.class,unitCode);
    }

    public int updateDept(Department department,String unitCode) {
        String sql = "UPDATE department SET unit_name = ?, manager = ? WHERE unit_code = ?";
        return update(sql,department.getUnitName(),department.getManager(),unitCode);
    }
}
