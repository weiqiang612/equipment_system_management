package com.weiqiang.dao;

import com.weiqiang.utils.DBUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * @author 袁志刚
 * @version 1.0
 * 完成一些基础的增删改查操作
 */

@Slf4j
public class BasicDao<T> {
    // 增删改
    public int update(String sql,Object... params){
        try(Connection connection = DBUtils.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            // 若传递参数不为空，给预处理语句赋参数值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1,params[i]);
                }
            }
            log.info("执行DML SQL: {} | 参数: {}", sql, Arrays.toString(params));
            return ps.executeUpdate();
        } catch (SQLException e) {
            log.error("SQL执行异常: {} | 参数: {}", sql, java.util.Arrays.toString(params), e);
            throw new RuntimeException(e);
        }
    }

    // 单值查询
    public Object singleSelect(String sql, Object... params){
        try(Connection connection = DBUtils.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            // 给预处理语句参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1,params[i]);
                }
            }
            try(ResultSet rs = ps.executeQuery()){
                // 将光标从当前位置向前移动一行。 ResultSet光标最初位于第一行之前; 第一次调用方法next使第一行成为当前行
                if (rs.next()) {
                    return rs.getObject(1);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 单行查询
    public <ResultType> ResultType selectOne(String sql,Class<ResultType> clazz,Object... params){
        List<ResultType> list = mutiSelect(sql, clazz, params);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        // 查询不到结果由业务层处理
        return null;
    }

    // 多行查询 根据所传入的泛型，决定要封装成什么类
    public <ResultType> List<ResultType> mutiSelect(String sql,Class<ResultType> clazz, Object... params){
        List<ResultType> list = new ArrayList<>();
        try(Connection connection = DBUtils.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){
            if (params != null) {
                // 预处理语句赋参数值
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1,params[i]);
                }
            }
            log.info("执行查询SQL: {} | 参数: {}", sql, Arrays.toString(params));
            // 执行查询语句
            try(ResultSet rs = ps.executeQuery()){
                // 获取有关ResultSet对象中列的类型和属性的信息的对象
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                // 使用反射，将查询结果封装到实体中
                while (rs.next()){
                    ResultType entity = clazz.getDeclaredConstructor().newInstance();
                    for (int i = 1; i <= columnCount; i++) {
                        // 获取该列列名和对应值 (要求数据库查询结果的列名和实体类属性一致，可以使用AS)
                        String columnLabel = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);

                        // 处理 java.sql.Date 转换为 java.time.LocalDate
                        if (value instanceof java.sql.Date) {
                            value = ((java.sql.Date) value).toLocalDate();
                        }
                        Field field = null;

                        try {
                            // 将获取到的列名和值赋给实体
                            // 注意：SQL数据库中的列名是下划线命名风格，而pojo实体类是驼峰命名风格，可以查询时
                            // 使用别名解决属性名不匹配问题
                            field = clazz.getDeclaredField(columnLabel);
                            // 处理驱动应该返回BigDecimal时返回其他类型的情况
                            // 检查实体类对应属性的类型，如果是 BigDecimal 而 value 是其他数字类型则转换
                            if (value != null && field.getType() == BigDecimal.class){
                                if (!(value instanceof BigDecimal)){
                                    value = new BigDecimal(value.toString());
                                }
                            }
                            // 爆破
                            field.setAccessible(true);
                            field.set(entity,value);
                        } catch (NoSuchFieldException e) {
                            // 防止SQL别名不匹配导致程序崩溃
                            log.warn("SQL结果列 " + columnLabel + " 在实体类 " + clazz.getSimpleName() + " 中找不到对应属性");
                        }
                    }
                    list.add(entity);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            // 记录日志
            log.error("执行 SQL 失败，SQL 语句为：{}", sql, e);
            throw new RuntimeException("数据库操作异常",e);
        }
        return list;
    }


    // 使用事务的增删改 关键点在于获取同一个连接
    // 使用LinkedHashMap，每一个SQL语句对应一组参数 将该LinkedHashMap当做参数传入,并可以保证执行顺序可控
    public boolean updateWithTransaction(LinkedHashMap<String,List<Object>> sqls) {
        // 记录事务操作耗时
        long startTime = System.currentTimeMillis();
        try (Connection connection = DBUtils.getConnection()) {
            connection.setAutoCommit(false);
            // 遍历所有sql语句
            String currSql = "";
            try {
                for (Map.Entry<String, List<Object>> entry : sqls.entrySet()) {
                    currSql = entry.getKey();
                    List<Object> params = entry.getValue();

                    try(PreparedStatement ps = connection.prepareStatement(currSql)) {
                        // 如果该sql的参数不为空 给预处理语句赋值
                        if (params != null) {
                            for (int i = 0; i < params.size(); i++) {
                                ps.setObject(i + 1, params.get(i));
                            }
                        }

                        // 执行sql
                        int count = ps.executeUpdate();
                        String sqlUpper = currSql.trim().toUpperCase();

                        if (count <= 0) {
                            if (sqlUpper.startsWith("UPDATE")) {
                                throw new SQLException("更新失败：未找到对应记录，事务回滚。SQL: " + currSql);
                            } else if (sqlUpper.startsWith("INSERT")) {
                                throw new SQLException("插入失败：数据未写入成功，事务回滚。SQL: " + currSql);
                            } else if (sqlUpper.startsWith("DELETE")) {
                                // 对于删除，删除可能失败
                                // throw new SQLException("删除失败：记录不存在，事务回滚。SQL: " + currSql);
                                log.warn("注意：删除操作影响行数为0。SQL: {}", currSql);
                            }
                        }
                        log.info("执行成功 | 影响行数: {} | SQL: {}", count, currSql);
                    }
                }
                // 只有所有语句都成功执行，才进行提交
                connection.commit();
                log.info("事务提交成功 | 总耗时: {}ms", (System.currentTimeMillis() - startTime));
                return true;
            } catch (SQLException e) {
                connection.rollback();
                log.error("执行SQL：{} 失败，正在回滚事务", currSql, e);
                if (e.getErrorCode() == 1451) {
                    log.error("删除失败：该记录已被其他表引用（如单位下仍有设备），请先清理关联数据。");
                    throw new RuntimeException("该记录正在使用中，无法删除");
                }
                throw new RuntimeException("事务执行失败，数据已回滚", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库连接失败", e);
        }
    }
}
