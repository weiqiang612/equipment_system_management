package com.weiqiang.service;

import com.weiqiang.pojo.TransferRecord;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */


public interface TransferRecordService {
    List<TransferRecord> getTransferRecords();

    TransferRecord getTransferRecordById(Integer transferId);

    boolean transferEquip(String equipId, TransferRecord transferRecord);

    int updateTransferRecord(Integer transferId,TransferRecord transferRecord);

    boolean deleteTransferRecord(Integer transferId);
}
