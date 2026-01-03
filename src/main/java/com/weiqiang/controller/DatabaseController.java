package com.weiqiang.controller;

import com.weiqiang.config.DBBackupProperties;
import com.weiqiang.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 袁志刚
 * @version 1.0
 */

@RestController
@RequestMapping("/api/system/db")
public class DatabaseController {

    @Autowired
    private DBBackupProperties backupProperties;


    @PostMapping("/backup")
    public Result backup() {
        String fileName = "backup_" + System.currentTimeMillis() + ".sql";
        File dir = new File(backupProperties.getPath());
        if (!dir.exists()) dir.mkdirs();

        // 这里的参数需要根据本地数据库的用户名、密码和库名修改
        String cmd = String.format("mysqldump -u%s -p%s %s -r %s",
                backupProperties.getUser(),
                backupProperties.getPassword(),
                backupProperties.getDatabase(),
                backupProperties.getPath() + fileName);

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            if (process.waitFor() == 0) {
                return Result.success("备份成功，文件名：" + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("备份执行失败，请检查数据库配置");
    }

    @PostMapping("/restore")
    public Result restore(@RequestParam("fileName") String fileName) {
        // 构造完整的备份文件路径
        String filePath = backupProperties.getPath() + fileName;

        // Windows 环境下需要调用 cmd /c 才能执行带 < 的重定向命令
        String cmd = String.format("cmd /c mysql -u%s -p%s equipment_management_system < %s",
                backupProperties.getUser(), backupProperties.getPassword(), filePath);

        try {
            Process process = Runtime.getRuntime().exec(cmd);

            // 关键点：如果不读取错误流，进程可能会阻塞，导致你一直等不到返回
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            if (process.waitFor() == 0) {
                return Result.success("数据已成功恢复");
            }
            // 设置一个超时保护，防止进程挂死
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return Result.error("恢复操作超时，请检查备份文件是否正确");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("恢复失败，请检查数据库权限或路径");
    }

    // 获取所有备份文件列表
    @GetMapping("/files")
    public Result listFiles() {
        File dir = new File(backupProperties.getPath());
        if (!dir.exists()) return Result.success(new ArrayList<>());

        // 获取以 .sql 结尾的文件
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));

        List<Map<String, Object>> fileInfoList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", file.getName()); // 文件名
                map.put("size", file.length());   // 文件大小
                map.put("lastModified", file.lastModified()); // 修改时间
                fileInfoList.add(map);
            }
        }
        // 按时间倒序排列，让最新的备份排在最前面
        fileInfoList.sort((a, b) -> Long.compare((Long)b.get("lastModified"), (Long)a.get("lastModified")));

        return Result.success(fileInfoList);
    }

    // 动态获取当前备份文件目录路径
    @GetMapping("/config")
    public Result getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("path", backupProperties.getPath());
        return Result.success(config);
    }
}
