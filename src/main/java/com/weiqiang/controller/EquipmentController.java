package com.weiqiang.controller;

import com.weiqiang.pojo.*;
import com.weiqiang.service.EquipmentService;
import com.weiqiang.service.MaintenanceRecordService;
import com.weiqiang.service.ScrapRecordService;
import com.weiqiang.service.TransferRecordService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/equipments")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private MaintenanceRecordService maintenanceRecordService;

    @Autowired
    private ScrapRecordService scrapRecordService;

    @Autowired
    private TransferRecordService transferRecordService;

//    // 查询所有设备信息
//    @GetMapping
//    public Result getEquipments(){
//        List<Equipment> equipments = equipmentService.getEquipments();
//        log.info("进行了查询设备的操作，结果数量为：{}",equipments.size());
//        return Result.success(equipments);
//    }

    // 根据ID查询设备信息
    @GetMapping("/{equipId}")
    public Result getEquipmentById(@PathVariable("equipId") String equipId) {
        Equipment equipment = equipmentService.getEquipmentById(equipId);
        log.info("进行了根据ID查询设备的操作，结果为：{}", equipment);
        return equipment != null ? Result.success(equipment) : Result.error("未查询到结果！");
    }

    // 动态SQL查询
    // 根据名称(支持模糊查询) 分类 所属单位 状态 购入日期 查询
    @GetMapping
    public Result getEquipmentsDynamic(
            @RequestParam(value = "equipName", required = false) String equipName,
            @RequestParam(value = "unitCode", required = false) String unitCode,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "status", required = false) String status,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(value = "begin", required = false) LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(value = "end", required = false) LocalDate end,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        PageBean equipments =
                equipmentService.getEquipmentsDynamic(equipName, unitCode, categoryId, status, begin, end, page, pageSize);
        log.info("进行了查询设备的操作，结果数量为：{}", equipments.getRows().size());
        return Result.success(equipments);
    }

    // 将动态SQL查询结果导出，由于要导出全部数据，所以不带分页参数 带有折旧信息
    @GetMapping("/export")
    public Result getEquipmentsDynamicForExport(
            @RequestParam(value = "equipName", required = false) String equipName,
            @RequestParam(value = "unitCode", required = false) String unitCode,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "status", required = false) String status,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(value = "begin", required = false) LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(value = "end", required = false) LocalDate end
    ) {
        List<EquipmentDepreciationVO> equipments =
                equipmentService.getEquipmentsDynamicForExport(equipName, unitCode, categoryId, status, begin, end);
        log.info("进行了导出设备的操作，结果数量为：{}", equipments.size());
        return Result.success(equipments);
    }


    // 添加设备
    @PostMapping
    public Result addEquipment(@RequestBody Equipment equipment) {
        try {
            int i = equipmentService.addEquipment(equipment);
            return i > 0 ? Result.success() : Result.error("添加设备失败!");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    // 根据ID更新设备
    @PutMapping("/{equipId}")
    public Result updateEquipment(@PathVariable String equipId, @RequestBody Equipment equipment) {
        equipment.setEquipId(equipId);
        int i = equipmentService.updateEquipment(equipment);
        return i > 0 ? Result.success() : Result.error("更新设备失败!");
    }

    // 删除设备 将检修表 报废表 调拨表的该设备移除
    @DeleteMapping("/{equipId}")
    public Result deleteEquipment(
            @PathVariable("equipId") String equipId
    ) {
        boolean success = equipmentService.deleteEquipment(equipId);
        return success ? Result.success() : Result.error("删除设备失败!");
    }

    // 检修设备 传递检修的设备编号
    @PostMapping("/maint/{equipId}")
    public Result maintenanceEquip(@PathVariable("equipId") String equipId, @RequestBody MaintenanceRecord maintenanceRecord) {
        boolean success = maintenanceRecordService.maintenanceEquip(equipId, maintenanceRecord);
        return success ? Result.success() : Result.error("将设备添加到维修表中失败!");
    }

    // 报废设备 传递报废的设备编号
    @PostMapping("/scrap/{equipId}")
    public Result scrapEquip(@PathVariable("equipId") String equipId, @RequestBody ScrapRecord scrapRecord) {
        boolean success = scrapRecordService.scrapEquip(equipId, scrapRecord);
        return success ? Result.success() : Result.error("将设备添加到报废表中失败!");
    }

    // 调拨设备
    // /equipments/transfer/${equipId}
    @PostMapping("/transfer/{equipId}")
    public Result transferEquip(@PathVariable("equipId") String equipId,@RequestBody TransferRecord transferRecord){
        boolean success = transferRecordService.transferEquip(equipId,transferRecord);
        return success ? Result.success() : Result.error("将设备添加到调拨表中失败!");
    }


    // 查看某台设备的折旧信息
    @GetMapping("/calculateAccumulated/{equipId}")
    public Result calculateAccumulated(@PathVariable("equipId") String equipId) {
        // 调用导出用的全量查询逻辑，但只查这一台
        List<EquipmentDepreciationVO> list = equipmentService.getEquipmentsDynamicForExport(equipId, null, null, null, null, null);

        if (list != null && !list.isEmpty()) {
            return Result.success(list.get(0));
        }
        return Result.error("未找到该设备的价值信息");
    }
}
