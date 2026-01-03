package com.weiqiang.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Data
@Component
@ConfigurationProperties(prefix = "project.db-backup")
public class DBBackupProperties {
    private String path;
    private String host;
    private String user;
    private String password;
    private String database;
}
