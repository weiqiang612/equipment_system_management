package com.weiqiang.controller;

import com.weiqiang.pojo.MaintenanceRecord;
import com.weiqiang.pojo.Result;
import com.weiqiang.pojo.ScrapRecord;
import com.weiqiang.service.ScrapRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 * 报废
 */

@Slf4j
@RestController
@RequestMapping("/scrapRecords")
public class ScrapRecordController {

    @Autowired
    private ScrapRecordService scrapRecordService;

    // 查询所有报废记录
    @GetMapping
    public Result getScrapRecords(){
        List<ScrapRecord> sr = scrapRecordService.getScrapRecords();
        log.info("进行了查询所有报废记录的操作，结果数量为：{}", sr.size());
        return Result.success(sr);
    }

    // 报废设备 传递报废的设备编号
    @PostMapping("/{equipId}")
    public Result scrapEquip(@PathVariable("equipId") String equipId, @RequestBody ScrapRecord scrapRecord){
        boolean success = scrapRecordService.scrapEquip(equipId,scrapRecord);
        return success ? Result.success() : Result.error("将设备添加到报废表中失败!");
    }


//     删除 用到了事务，将设备从报废表中删除，并将设备状态设为在用
    @DeleteMapping("/{scrapNo}")
    public Result deleteScrapRecord(
            @PathVariable("scrapNo") String scrapNo,
            @RequestParam("equipId") String equipId
    ){
        boolean success = scrapRecordService.deleteScrapRecord(equipId,scrapNo);
        return success ? Result.success() : Result.error("将设备从报废表中删除失败！");
    }

    // 修改操作，原则上不允许修改本表的报废单号、设备编号 其他记录可以修改
    @PutMapping("/{scrapNo}")
    public Result putScrapRecord(@PathVariable("scrapNo") String scrapNo,@RequestBody ScrapRecord scrapRecord){
        int i = scrapRecordService.putScrapRecord(scrapNo,scrapRecord);
        return i > 0 ? Result.success() : Result.error("更新报废表失败！");
    }
}
