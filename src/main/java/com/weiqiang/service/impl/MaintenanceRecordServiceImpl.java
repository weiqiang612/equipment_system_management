package com.weiqiang.service.impl;

import com.weiqiang.dao.EquipmentDao;
import com.weiqiang.dao.MaintenanceRecordDao;
import com.weiqiang.pojo.Equipment;
import com.weiqiang.pojo.MaintenanceRecord;
import com.weiqiang.service.MaintenanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Service
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {

    @Autowired
    private MaintenanceRecordDao maintenanceRecordDao;

    @Autowired
    private EquipmentDao equipmentDao;

    @Override
    public List<MaintenanceRecord> getMaintenanceRecords() {
        return maintenanceRecordDao.getMaintenanceRecords();
    }

    @Override
    public boolean maintenanceEquip(String equipId,MaintenanceRecord maintenanceRecord) {
        Equipment equipment = equipmentDao.getEquipmentById(equipId);
        if (equipment == null) {
            return false;
        }
        // 只有在用状态的设备才可以维修
        if ("维修".equals(equipment.getStatus())) {
            throw new RuntimeException("操作失败：该设备已在维修中！");
        }
        if ("报废".equals(equipment.getStatus())) {
            throw new RuntimeException("操作失败：该设备已报废，无法维修！");
        }

        return maintenanceRecordDao.maintenanceEquip(equipId,maintenanceRecord);
    }

    @Override
    public boolean deleteMaintenanceRecords(String equipId,Integer maintId) {
        return maintenanceRecordDao.deleteMaintenanceRecords(equipId,maintId);
    }

    @Override
    public int putMaintenanceRecords(Integer maintId, MaintenanceRecord maintenanceRecord) {
        return maintenanceRecordDao.putMaintenanceRecords(maintId,maintenanceRecord);
    }
}
