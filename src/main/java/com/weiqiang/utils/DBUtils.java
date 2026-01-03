package com.weiqiang.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Component
public class DBUtils {

    @Autowired
    private DataSource dataSource;

    private static DataSource staticDataSource;

    // 初始化静态变量，将注入的对象赋值给静态变量
    @PostConstruct
    public void init(){
        staticDataSource = this.dataSource;
    }

    // 获取连接
    public static Connection getConnection(){
        try {
            return staticDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 手动释放连接
    public static void close(Connection connection, ResultSet resultSet, Statement statement){
        try {
            if (connection != null) connection.close();
            if (resultSet != null)  resultSet.close();
            if (statement != null) statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
