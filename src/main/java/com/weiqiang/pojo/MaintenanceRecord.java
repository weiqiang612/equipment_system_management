package com.weiqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author 袁志刚
 * @version 1.0
 * 设备检修信息表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecord {
    private Integer maintId; // 维修单号
    private String equipId; // 设备编号
    private LocalDate maintDate; // 检修日期
    private String maintContent; // 检修内容描述
    private BigDecimal maintCost; // 检修费用
    private String maintPerson; // 检修人
}
