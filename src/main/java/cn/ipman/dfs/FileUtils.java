package cn.ipman.dfs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IOUtils;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
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

    public static String getUUIDFile(String file) {
        return UUID.randomUUID() + getExt(file);
    }

    public static String getExt(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    public static String getSubDir(String file) {
        return file.substring(0, 2);
    }

    public static void main(String[] args) {
        System.out.println(getMimeType("dfs.txt"));
    }

    @SneakyThrows
    public static void writeMeta(File metaFile, FileMeta meta) {
        String json = JSON.toJSONString(meta);
        Files.writeString(Paths.get(metaFile.toURI()), json,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }


    @SneakyThrows
    public static void writeString(File file, String content) {
        Files.writeString(Paths.get(file.toURI()), content,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    @SneakyThrows
    public static void download(String download, File file) {
        System.out.println(" =======> download file: " + file.getAbsoluteFile());
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Resource> exchange =
                restTemplate.exchange(download, HttpMethod.GET, entity, Resource.class);

        InputStream fis = new BufferedInputStream(Objects.requireNonNull(exchange.getBody()).getInputStream());
        byte[] buffer = new byte[16 * 1024];

        // 读取文件信息, 并逐段输出
        OutputStream outputStream = new FileOutputStream(file);
        while (fis.read(buffer) != -1) {
            outputStream.write(buffer);
        }
        outputStream.flush();
        outputStream.close();
        fis.close();
    }
}
