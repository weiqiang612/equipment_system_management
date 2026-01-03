package com.weiqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 袁志刚
 * @version 1.0
 * 设备使用单位代码表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    private String unitCode; // 单位代码
    private String unitName; // 单位名称
    private String manager; // 负责人
}
