package com.weiqiang.dao;

import com.weiqiang.pojo.MaintenanceRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 * 对设备检修表维护
 */

@Repository
public class MaintenanceRecordDao extends BasicDao<MaintenanceRecord>{
    public List<MaintenanceRecord> getMaintenanceRecords() {
        String sql = "SELECT maint_id maintId, equip_id equipId, maint_date maintDate, maint_content maintContent, " +
                "maint_cost maintCost, maint_person maintPerson " +
                "FROM maintenance_record";
        return mutiSelect(sql,MaintenanceRecord.class,null);
    }

    /*
    UPDATE equipment SET `status` = '维修' WHERE equip_id = 'E2024015';

    INSERT INTO maintenance_record (equip_id, maint_date, maint_content, maint_cost, maint_person) VALUES
    ('E2024015', '2024-12-05', '更换主板和屏幕', 1200.00, '校内维保组');

    */
    public boolean maintenanceEquip(String equipId,MaintenanceRecord maintenanceRecord) {
        String sql1 = "UPDATE equipment SET `status` = '维修' WHERE equip_id = ?";
        String sql2 = "INSERT INTO maintenance_record (equip_id, maint_date, maint_content, maint_cost, maint_person) VALUES  " +
                "(?, ?, ?, ?, ?)";
        ArrayList<Object> params1 = new ArrayList<>();
        params1.add(equipId);
        ArrayList<Object> params2 = new ArrayList<>();
        params2.add(maintenanceRecord.getEquipId());
        params2.add(maintenanceRecord.getMaintDate());
        params2.add(maintenanceRecord.getMaintContent());
        params2.add(maintenanceRecord.getMaintCost());
        params2.add(maintenanceRecord.getMaintPerson());

        LinkedHashMap<String, List<Object>> sqlTasks = new LinkedHashMap<>();
        sqlTasks.put(sql1, params1);
        sqlTasks.put(sql2, params2);
        return updateWithTransaction(sqlTasks);
    }

    /*
        DELETE FROM maintenance_record WHERE maint_id = ?;
        UPDATE equipment SET `status` = '在用' WHERE equip_id = 'E2024014';
    */
    // 先将记录从维修表中删除 后将设备状态设为在用
    public boolean deleteMaintenanceRecords(String equipId,Integer maintId) {
        String sql1 = "DELETE FROM maintenance_record WHERE maint_id = ?";
        String sql2 = "UPDATE equipment SET `status` = '在用' WHERE equip_id = ?";
        List<Object> params1 = new ArrayList<>();
        params1.add(maintId);
        List<Object> params2 = new ArrayList<>();
        params2.add(equipId);
        LinkedHashMap<String, List<Object>> sqlTasks = new LinkedHashMap<>();
        sqlTasks.put(sql1,params1);
        sqlTasks.put(sql2,params2);
        return updateWithTransaction(sqlTasks);
    }

    public int putMaintenanceRecords(Integer maintId, MaintenanceRecord maintenanceRecord) {
        String sql = "UPDATE maintenance_record  " +
                "SET maint_date = ?,maint_content = ?,maint_cost = ?, " +
                "maint_person = ? " +
                "WHERE maint_id = ?";
        return update(sql,maintenanceRecord.getMaintDate(),maintenanceRecord.getMaintContent(),
                maintenanceRecord.getMaintCost(),maintenanceRecord.getMaintPerson(),maintId);
    }
}
