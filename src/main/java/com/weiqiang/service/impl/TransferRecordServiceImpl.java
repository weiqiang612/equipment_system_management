package com.weiqiang.service.impl;

import com.weiqiang.dao.TransferRecordDao;
import com.weiqiang.pojo.TransferRecord;
import com.weiqiang.service.TransferRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Service
public class TransferRecordServiceImpl implements TransferRecordService {

    @Autowired
    private TransferRecordDao transferRecordDao;

    @Override
    public List<TransferRecord> getTransferRecords() {
        return transferRecordDao.getTransferRecords();
    }

    @Override
    public TransferRecord getTransferRecordById(Integer transferId) {
        return transferRecordDao.getTransferRecordById(transferId);
    }

    @Override
    public boolean transferEquip(String equipId, TransferRecord transferRecord) {
        // 不可以从本部门调到本部门
        if (transferRecord.getOutUnitCode().equals(transferRecord.getInUnitCode())){
            throw new RuntimeException("不可以从本部门调到本部门！");
        }

        return transferRecordDao.transferEquip(equipId,transferRecord);
    }

    @Override
    public int updateTransferRecord(Integer transferId,TransferRecord transferRecord) {
        return transferRecordDao.updateTransferRecord(transferId,transferRecord);
    }

    @Override
    public boolean deleteTransferRecord(Integer transferId) {
        // 将原单位代码查询得到后再给下层
        TransferRecord recordById = transferRecordDao.getTransferRecordById(transferId);
        String equipId = recordById.getEquipId();
        String outUnitCode = recordById.getOutUnitCode();
        return transferRecordDao.deleteTransferRecord(transferId,equipId,outUnitCode);
    }
}
