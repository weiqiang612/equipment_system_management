package com.weiqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author 袁志刚
 * @version 1.0
 * 设备报废信息表
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapRecord {
    private String equipId; // 设备编号
    private String scrapNo; // 报废单号
    private LocalDate scrapDate; // 报废日期
    private String approver; // 审批人
    private String reason; // 报废原因
}
