package com.weiqiang.dao;


import com.weiqiang.pojo.ScrapRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Repository
public class ScrapRecordDao extends BasicDao<ScrapRecord> {
    public List<ScrapRecord> getScrapRecords() {
        String sql = "SELECT equip_id equipId,scrap_no scrapNo,scrap_date scrapDate,approver,reason " +
                "FROM scrap_record";
        return mutiSelect(sql,ScrapRecord.class,null);
    }


    /*
    UPDATE equipment SET `status` = '报废' WHERE equip_id = 'E2024014';
    INSERT INTO scrap_record (equip_id, scrap_no, scrap_date, approver, reason) VALUES
    ('E2024014', 'SCRAP-2025-001', '2025-12-10', '资产处老王', '硬件老化无法修复');
    */
    // 报废操作，涉及到事务
    // 先将设备设为报废状态，再将记录添加到报废表中
    public boolean scrapEquip(String equipId, ScrapRecord scrapRecord) {
        String sql1 = "UPDATE equipment SET `status` = '报废' WHERE equip_id = ?";
        String sql2 = "INSERT INTO scrap_record (equip_id, scrap_no, scrap_date, approver, reason) VALUES " +
                "    (?, ?, ?, ?, ?)";
        ArrayList<Object> params1 = new ArrayList<>();
        params1.add(equipId);
        ArrayList<Object> params2 = new ArrayList<>();
        params2.add(scrapRecord.getEquipId());
        params2.add(scrapRecord.getScrapNo());
        params2.add(scrapRecord.getScrapDate());
        params2.add(scrapRecord.getApprover());
        params2.add(scrapRecord.getReason());

        LinkedHashMap<String, List<Object>> sqlTasks = new LinkedHashMap<>();
        sqlTasks.put(sql1, params1);
        sqlTasks.put(sql2, params2);
        return updateWithTransaction(sqlTasks);
    }

    /*
    DELETE FROM scrap_record WHERE scrap_no = 'SCRAP-2025-001';
    UPDATE equipment SET `status` = '在用' WHERE equip_id = 'E2024014';
    */
    // 删除报废记录
    // 将设备从表中删除，然后将设备状态设为在用，
    public boolean deleteScrapRecord(String equipId, String scrapNo) {
        String sql1 = "DELETE FROM scrap_record WHERE scrap_no = ?";
        String sql2 = "UPDATE equipment SET `status` = '在用' WHERE equip_id = ?";
        List<Object> params1 = new ArrayList<>();
        params1.add(scrapNo);
        List<Object> params2 = new ArrayList<>();
        params2.add(equipId);
        LinkedHashMap<String, List<Object>> sqlTasks = new LinkedHashMap<>();
        sqlTasks.put(sql1,params1);
        sqlTasks.put(sql2,params2);
        return updateWithTransaction(sqlTasks);
    }

    // 修改报废表除报废单号和设备编号外的其他字段
    /*
    UPDATE scrap_record SET scrap_date = '2025-12-10',approver = '资产处老李',reason = '硬件老化无法修复'
    WHERE scrap_no = 'SCRAP-2025-001';
    */
    public int putScrapRecord(String scrapNo, ScrapRecord scrapRecord) {
        String sql = "UPDATE scrap_record SET scrap_date = ?,approver = ?,reason = ?  " +
                "    WHERE scrap_no = ?";
        return update(sql,scrapRecord.getScrapDate(),scrapRecord.getApprover(),scrapRecord.getReason(),
                scrapNo);
    }
}
