package cn.ipman.dfs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/7/21 12:43
 */
@ConfigurationProperties(prefix = "dfs")
@Data
public class DfsConfigProperties {
    private String uploadPath;
    private String backupUrl;
    private String downloadUrl;
    private String group;
    private boolean autoMd5;
    private boolean syncBackup;
}
