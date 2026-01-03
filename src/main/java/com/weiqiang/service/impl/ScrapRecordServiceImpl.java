package com.weiqiang.service.impl;

import com.weiqiang.dao.EquipmentDao;
import com.weiqiang.dao.ScrapRecordDao;
import com.weiqiang.pojo.Equipment;
import com.weiqiang.pojo.ScrapRecord;
import com.weiqiang.service.ScrapRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Service
public class ScrapRecordServiceImpl implements ScrapRecordService {

    @Autowired
    private ScrapRecordDao scrapRecordDao;

    @Autowired
    private EquipmentDao equipmentDao;

    @Override
    public List<ScrapRecord> getScrapRecords() {
        return scrapRecordDao.getScrapRecords();
    }

    @Override
    public boolean scrapEquip(String equipId, ScrapRecord scrapRecord) {
        Equipment equipment = equipmentDao.getEquipmentById(equipId);
        if (equipment == null) {
            return false;
        }

        // 报废状态的设备无法再报废
        if ("报废".equals(equipment.getStatus())) {
            throw new RuntimeException("操作失败：该设备已报废，无法再报废！");
        }

        // 生成报废单号 (例如：BF + 当前时间)
        // 并发量并不大，所以可以使用 ss 秒作为报废单号一部分就可以保证唯一性
        String generatedNo = "BF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH-ss"));
        scrapRecord.setScrapNo(generatedNo);

        // 2. 确保设备 ID 一致
        scrapRecord.setEquipId(equipId);

        return scrapRecordDao.scrapEquip(equipId,scrapRecord);
    }

    @Override
    public boolean deleteScrapRecord(String equipId, String scrapNo) {
        return scrapRecordDao.deleteScrapRecord(equipId,scrapNo);
    }

    @Override
    public int putScrapRecord(String scrapNo, ScrapRecord scrapRecord) {
        return scrapRecordDao.putScrapRecord(scrapNo,scrapRecord);
    }
}
