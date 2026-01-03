package com.weiqiang.service;

import com.weiqiang.pojo.MaintenanceRecord;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */


public interface MaintenanceRecordService {
    List<MaintenanceRecord> getMaintenanceRecords();

    boolean maintenanceEquip(String equipId,MaintenanceRecord maintenanceRecord);

    boolean deleteMaintenanceRecords(String equipId,Integer maintId);

    int putMaintenanceRecords(Integer maintId, MaintenanceRecord maintenanceRecord);
}
