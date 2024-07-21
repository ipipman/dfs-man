package cn.ipman.dfs.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件元数据类，用于存储文件的元信息。
 * 包括文件名、原始文件名、文件大小、下载链接以及标签。
 *
 * @Author IpMan
 * @Date 2024/7/20 14:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMeta {

    /**
     * 文件名，用于唯一标识一个文件。
     */
    private String name;

    /**
     * 原始文件名，保留用户上传时的文件名。
     */
    private String originalFileName;

    /**
     * 文件大小，以字节为单位。
     */
    private long size;

    /**
     * 文件下载URL，提供文件下载的地址。
     */
    private String downloadUrl;

    /**
     * 文件标签，使用Map存储，键值对形式表示，用于标记或分类文件。
     * 标签可用于快速搜索或筛选文件。
     */
    private Map<String, String> tags = new HashMap<>();

}
