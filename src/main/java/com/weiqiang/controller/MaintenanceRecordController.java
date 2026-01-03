package com.weiqiang.controller;

import com.weiqiang.pojo.MaintenanceRecord;
import com.weiqiang.pojo.Result;
import com.weiqiang.service.MaintenanceRecordService;
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
@RequestMapping("/maintenanceRecords")
public class MaintenanceRecordController {

    @Autowired
    private MaintenanceRecordService maintenanceRecordService;

    // 后续可以做分页
    @GetMapping
    public Result getMaintenanceRecords(){
        List<MaintenanceRecord> mr = maintenanceRecordService.getMaintenanceRecords();
        log.info("进行了查询所有检修记录的操作，结果数量为：{}", mr.size());
        return Result.success(mr);
    }

    // 这里也提供将设备检修的操作
    // 添加 属于设备那边的操作 用到了事务，将设备的状态改为 维修 ，并在维修表添加一条记录
    // 检修设备 传递检修的设备编号
    @PostMapping("/{equipId}")
    public Result maintenanceEquip(@PathVariable("equipId") String equipId, @RequestBody MaintenanceRecord maintenanceRecord){
        boolean success = maintenanceRecordService.maintenanceEquip(equipId,maintenanceRecord);
        return success ? Result.success() : Result.error("将设备添加到维修表中失败!");
    }

    // 删除 用到了事务，将设备从维修表中删除，并将设备状态设为在用
    @DeleteMapping("/{maintId}")
    public Result deleteMaintenanceRecords(
            @PathVariable("maintId") Integer maintId,
            @RequestParam("equipId") String equipId
    ){
        boolean success = maintenanceRecordService.deleteMaintenanceRecords(equipId,maintId);
        return success ? Result.success() : Result.error("将设备从维修表中删除失败！");
    }

    // 修改操作，原则上不允许修改本表的检修单号、设备编号 其他记录可以修改
    @PutMapping("/{maintId}")
    public Result putMaintenanceRecords(@PathVariable("maintId") Integer maintId,@RequestBody MaintenanceRecord maintenanceRecord){
        int i = maintenanceRecordService.putMaintenanceRecords(maintId,maintenanceRecord);
        return i > 0 ? Result.success() : Result.error("更新维修表失败！");
    }


}
