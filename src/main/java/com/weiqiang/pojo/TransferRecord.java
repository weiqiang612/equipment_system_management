package com.weiqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author 袁志刚
 * @version 1.0
 * 设备调拨信息表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRecord {
    private Integer transferId; // 调拨单号
    private String equipId; // 设备编号
    private String outUnitCode; // 原单位代码
    private String inUnitCode; // 新单位代码
    private LocalDate transferDate; // 调拨日期
    private String changeType; // 变动类型
    private String operator; // 经办人
    private String reason; // 调拨原因

    // 优化展示信息
    private String equipName;
    private String outUnitName;
    private String inUnitName;
}
