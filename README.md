# equipment_system_management
数据库课设作业，基于 Springboot + MySQL + Vue2 + ElementUI 开发的一个简单的设备管理系统。应课设要求，持久层使用了原生的JDBC开发。
需要在配置文件（application.yml 或 新建一个 application-dev.yml）中添加本地配置信息，以下为模版：
```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/equipment_management_system
    username: root
    password: 123456
# 备份数据库功能配置信息
project:
  db-backup:
    path: D:/backup/equipment
    host: localhost
    user: root
    password: 123456
    database: equipment_management_system
    
```
