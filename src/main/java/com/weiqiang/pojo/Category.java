package com.weiqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author 袁志刚
 * @version 1.0
 * 国家标准设备分类表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private String categoryId; // 国家标准的分类编码
    private String categoryName; // 分类名称
    private Integer usefulLife; // 预计使用年限
    private BigDecimal residualRate; // 残值率
}
