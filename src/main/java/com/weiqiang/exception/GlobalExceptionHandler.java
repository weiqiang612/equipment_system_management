package com.weiqiang.exception;

import com.weiqiang.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author 袁志刚
 * @version 1.0
 * 全局异常处理器
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统运行异常：", e);

        // 判断是否为数据库主键重复异常
        if (e.getCause() instanceof SQLIntegrityConstraintViolationException ||
                e.getMessage().contains("Duplicate entry")) {
            return Result.error("操作失败：唯一编号已存在，请勿重复添加！");
        }

        return Result.error("操作失败：" + e.getMessage());
    }
}
