package cn.ipman.dfs;

import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * Utils for file.
 *
 * @Author IpMan
 * @Date 2024/7/20 12:57
 */
public class FileUtils {

    static String DEFAULT_MIME_TYPE = "application/octet-stream";

    public static String getMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String content = fileNameMap.getContentTypeFor(fileName);
        return content == null ? DEFAULT_MIME_TYPE : content;
    }


    public static void main(String[] args) {
        System.out.println(getMimeType("dfs.txt"));
    }

}
