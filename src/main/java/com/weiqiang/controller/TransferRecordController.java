package com.weiqiang.controller;

import com.weiqiang.pojo.Result;
import com.weiqiang.pojo.TransferRecord;
import com.weiqiang.service.TransferRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/transferRecords")
public class TransferRecordController {

    @Autowired
    private TransferRecordService transferRecordService;

    @GetMapping
    // 查询所有调拨记录信息
    public Result getTransferRecords(){
        List<TransferRecord> transferRecords = transferRecordService.getTransferRecords();
        log.info("进行了查询所有调拨记录的操作，结果的个数为：{}",transferRecords.size());
        return Result.success(transferRecords);
    }

    // 根据ID查询调拨信息
    @GetMapping("/{transferId}")
    public Result getTransferRecordById(@PathVariable("transferId") Integer transferId){
        TransferRecord transferRecord = transferRecordService.getTransferRecordById(transferId);
        return transferRecord != null ? Result.success(transferRecord) : Result.error("未查询到相关记录！");
    }

    // 调拨设备
    @PostMapping("/{equipId}")
    public Result transferEquip(@PathVariable("equipId") String equipId,@RequestBody TransferRecord transferRecord){
        boolean success = transferRecordService.transferEquip(equipId,transferRecord);
        return success ? Result.success() : Result.error("将设备添加到调拨表中失败!");
    }

    // 更新调拨信息
    // 不允许修改设备编号、调拨编号、出入单位代码
    @PutMapping("/{transferId}")
    public Result updateTransferRecord(@PathVariable("transferId") Integer transferId,@RequestBody TransferRecord transferRecord){
        int i = transferRecordService.updateTransferRecord(transferId,transferRecord);
        return i > 0 ? Result.success() : Result.error("修改调拨信息失败！");
    }

    // 删除调拨信息
    // 将设备调回原单位
    @DeleteMapping("/{transferId}")
    public Result deleteTransferRecord(@PathVariable("transferId")Integer transferId){
        boolean success = transferRecordService.deleteTransferRecord(transferId);
        return success ? Result.success() : Result.error("将设备从调拨表中删除失败!");
    }


}
