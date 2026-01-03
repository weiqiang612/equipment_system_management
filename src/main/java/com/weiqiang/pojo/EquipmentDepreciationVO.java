package com.weiqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author 袁志刚
 * @version 1.0
 * 将折旧计算数据封装返回前端
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDepreciationVO {
    private String equipId;
    private String equipName;
    private String categoryName;
    private String unitName;

    private BigDecimal originalValue;   // 原值
    private LocalDate purchaseDate;    // 购入日期

    // 核心计算结果
    private BigDecimal monthlyDepreciation; // 月折旧额
    private BigDecimal accumulated;         // 累计折旧
    private BigDecimal netValue;            // 当前净值

    // 业务参数
    private Integer usefulLife;     // 预计使用年限（年）
    private BigDecimal residualRate;    // 残值率
    private String status;

    // 状态标识
    private Boolean isFullyDepreciated; // 是否提足折旧（如果净值=残值，设为true）
}
