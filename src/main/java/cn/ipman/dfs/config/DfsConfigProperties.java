package cn.ipman.dfs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 分布式文件系统（DFS）配置属性类。
 * 用于绑定配置文件中以"dfs"为前缀的属性值。
 *
 * @Author IpMan
 * @Date 2024/7/21 12:43
 */
@ConfigurationProperties(prefix = "dfs")
@Data
public class DfsConfigProperties {
    /**
     * 文件上传路径配置。
     */
    private String uploadPath;

    /**
     * 文件备份URL配置。
     */
    private String backupUrl;

    /**
     * 文件下载URL配置。
     */
    private String downloadUrl;

    /**
     * 文件分组配置。
     */
    private String group;

    /**
     * 是否自动计算文件MD5配置。
     */
    private boolean autoMd5;

    /**
     * 是否同步备份配置。
     * 开启后，文件上传时会同步进行备份，提高数据的安全性。
     */
    private boolean syncBackup;
}
