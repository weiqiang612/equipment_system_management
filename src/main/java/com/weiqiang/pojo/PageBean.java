package com.weiqiang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBean {
    private Long total; // 分页查询返回的记录总条数
    private List<Equipment> rows; // 分页查询返回的数据
}
