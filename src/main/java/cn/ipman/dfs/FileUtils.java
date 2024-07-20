package cn.ipman.dfs;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.UUID;

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


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init(String uploadPath) {
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        for (int i = 0; i < 256; i++) {
            String subDir = String.format("%02x", i); // 16进制
            File file = new File(uploadPath + "/" + subDir);
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }

    public static String getUUIDFile(String file){
        return UUID.randomUUID() + "." + getExt(file);
    }

    public static String getExt(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    public static void main(String[] args) {
        System.out.println(getMimeType("dfs.txt"));
    }

}
