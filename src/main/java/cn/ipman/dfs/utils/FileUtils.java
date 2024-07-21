package cn.ipman.dfs.utils;

import cn.ipman.dfs.meta.FileMeta;
import com.alibaba.fastjson.JSON;
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
 * 文件工具类，提供文件相关操作的便利方法。
 *
 * @Author IpMan
 * @Date 2024/7/20 12:57
 */
public class FileUtils {

    /**
     * 默认的MIME类型，用于当无法确定文件类型时。
     */
    static String DEFAULT_MIME_TYPE = "application/octet-stream";


    /**
     * 根据文件名获取文件的MIME类型。
     *
     * @param fileName 文件名
     * @return 文件的MIME类型
     */
    public static String getMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String content = fileNameMap.getContentTypeFor(fileName);
        return content == null ? DEFAULT_MIME_TYPE : content;
    }

    /**
     * 初始化上传目录，包括创建主目录和256个子目录。
     *
     * @param uploadPath 上传目录路径
     */
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

    /**
     * 生成带UUID的文件名。
     *
     * @param file 原始文件名
     * @return 带UUID的文件名
     */
    public static String getUUIDFile(String file) {
        return UUID.randomUUID() + getExt(file);
    }

    /**
     * 获取文件的扩展名。
     *
     * @param originalFilename 原始文件名
     * @return 文件的扩展名
     */
    public static String getExt(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    /**
     * 获取文件的子目录名，基于文件名的前两个字符。
     *
     * @param file 文件名
     * @return 文件的子目录名
     */
    public static String getSubDir(String file) {
        return file.substring(0, 2);
    }

    public static void main(String[] args) {
        System.out.println(getMimeType("dfs.txt"));
    }


    /**
     * 将文件元信息写入文件。
     *
     * @param metaFile 文件元信息文件
     * @param meta     文件元信息
     * @throws IOException 写入文件时发生IO异常
     */
    @SneakyThrows
    public static void writeMeta(File metaFile, FileMeta meta) {
        String json = JSON.toJSONString(meta);
        Files.writeString(Paths.get(metaFile.toURI()), json,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }


    /**
     * 将字符串内容写入文件。
     *
     * @param file    文件
     * @param content 内容
     * @throws IOException 写入文件时发生IO异常
     */
    @SneakyThrows
    public static void writeString(File file, String content) {
        Files.writeString(Paths.get(file.toURI()), content,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    /**
     * 下载文件。
     *
     * @param download 下载链接
     * @param file     保存的文件
     * @throws IOException 读写文件时发生IO异常
     */
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
