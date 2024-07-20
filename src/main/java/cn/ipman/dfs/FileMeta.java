package cn.ipman.dfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * file meta data.
 *
 * @Author IpMan
 * @Date 2024/7/20 14:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMeta {

    private String name;
    private String originalFileName;
    private long size;
    //private String md5;
    private Map<String, String> tags = new HashMap<>();

}
