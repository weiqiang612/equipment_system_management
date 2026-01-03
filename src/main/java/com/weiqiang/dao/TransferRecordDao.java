package com.weiqiang.dao;

import com.weiqiang.pojo.TransferRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Repository
public class TransferRecordDao extends BasicDao<TransferRecord> {
    public List<TransferRecord> getTransferRecords() {
        String sql = "SELECT transfer_id transferId, t.equip_id equipId,equip_name equipName, " +
                " out_unit_code outUnitCode, d1.unit_name outUnitName, " +
                " in_unit_code inUnitCode, d2.unit_name inUnitName,transfer_date transferDate, change_type changeType, operator, reason " +
                "FROM transfer_record t " +
                "LEFT JOIN " +
                " equipment e ON t.equip_id = e.equip_id" +
                " LEFT JOIN " +
                " department d1 ON t.out_unit_code = d1.unit_code " +
                " LEFT JOIN " +
                " department d2 ON t.in_unit_code = d2.unit_code " +
                "ORDER BY " +
                " t.transfer_date DESC ";
        return mutiSelect(sql,TransferRecord.class,null);
    }

    public TransferRecord getTransferRecordById(Integer transferId) {
        String sql = "SELECT transfer_id transferId, t.equip_id equipId,equip_name equipName, " +
                " out_unit_code outUnitCode, d1.unit_name outUnitName, " +
                " in_unit_code inUnitCode, d2.unit_name inUnitName,transfer_date transferDate, change_type changeType, operator, reason " +
                "FROM transfer_record t " +
                "LEFT JOIN " +
                " equipment e ON t.equip_id = e.equip_id" +
                " LEFT JOIN " +
                " department d1 ON t.out_unit_code = d1.unit_code " +
                " LEFT JOIN " +
                " department d2 ON t.in_unit_code = d2.unit_code " +
                " WHERE transfer_id = ? " +
                " ORDER BY " +
                " t.transfer_date DESC ";
        return selectOne(sql, TransferRecord.class,transferId);
    }
/*

    调拨 两步操作，先将设备部门改了 之后在调拨表中插入一条记录
    UPDATE equipment SET unit_code = 'D01' WHERE equip_id = 'E2024015';

    INSERT INTO transfer_record (equip_id, out_unit_code, in_unit_code, transfer_date, change_type, operator, reason) VALUES
    ('E2024015', 'D03', 'D01', '2024-12-01', '部门调拨', '管理员A', '教学办公需要')
*/

    public boolean transferEquip(String equipId, TransferRecord transferRecord) {
        String sql1 = "UPDATE equipment SET unit_code = ? WHERE equip_id = ?";
        List<Object> params1 = new ArrayList<>();
        params1.add(transferRecord.getInUnitCode());
        params1.add(equipId);
        String sql2 = "INSERT INTO transfer_record (equip_id, out_unit_code, in_unit_code, transfer_date, change_type, operator, reason) VALUES  " +
                "    (?, ?, ?, ?, ?, ?, ?)";
        List<Object> params2 = new ArrayList<>();
        params2.add(equipId);
        params2.add(transferRecord.getOutUnitCode());
        params2.add(transferRecord.getInUnitCode());
        params2.add(transferRecord.getTransferDate());
        params2.add(transferRecord.getChangeType());
        params2.add(transferRecord.getOperator());
        params2.add(transferRecord.getReason());
        LinkedHashMap<String, List<Object>> sqlTasks = new LinkedHashMap<>();
        sqlTasks.put(sql1,params1);
        sqlTasks.put(sql2,params2);
        return updateWithTransaction(sqlTasks);
    }

    public int updateTransferRecord(Integer transferId,TransferRecord transferRecord) {
        String sql = "UPDATE transfer_record SET transfer_date = ?," +
                "change_type = ?,operator = ?,reason = ? WHERE transfer_id = ?";
        return update(sql,transferRecord.getTransferDate(),transferRecord.getChangeType(),
                transferRecord.getOperator(),transferRecord.getReason(),transferId);
    }

    // 将设备单位信息改回原单位代码 并从表中删除
    public boolean deleteTransferRecord(Integer transferId,String equipId,String outUnitCode) {
        String sql1 = "UPDATE equipment SET unit_code = ? WHERE equip_id = ?";
        List<Object> params1 = new ArrayList<>();
        params1.add(outUnitCode);
        params1.add(equipId);
        String sql2 = "DELETE FROM transfer_record WHERE transfer_id = ?";
        List<Object> params2 = new ArrayList<>();
        params2.add(transferId);
        LinkedHashMap<String, List<Object>> sqlTasks = new LinkedHashMap<>();
        sqlTasks.put(sql1,params1);
        sqlTasks.put(sql2,params2);
        return updateWithTransaction(sqlTasks);
    }
}
