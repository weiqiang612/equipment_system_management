package com.weiqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author 袁志刚
 * @version 1.0
 * 设备信息表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {
    private String equipId; // 设备编号
    private String equipName; // 设备名称
    private String model; // 规格型号
    private String status; // 设备状态 '在用','维修','报废'
    private LocalDate purchaseDate; // 购入日期
    private BigDecimal originalValue; // 原值
    private String unitCode; // 当前单位代码
    private String categoryId; // 分类编码
    // 用于多表联查
    private String unitName; // 所属单位名称
    private String categoryName; // 所属分类名称

    // 用于之后的折旧计算，一次查询把所需参数查询出来，避免了之后在封装EquipmentDepreciationVO类的时候进行多次查询
    private Integer usefulLife;
    private BigDecimal residualRate;
}
