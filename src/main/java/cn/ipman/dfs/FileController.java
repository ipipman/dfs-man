package cn.ipman.dfs;

import cn.ipman.dfs.config.DfsConfigProperties;
import cn.ipman.dfs.meta.FileMeta;
import cn.ipman.dfs.syncer.HttpSyncer;
import cn.ipman.dfs.syncer.MQSyncer;
import cn.ipman.dfs.utils.FileUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import static cn.ipman.dfs.utils.FileUtils.getMimeType;
import static cn.ipman.dfs.utils.FileUtils.getUUIDFile;
import static cn.ipman.dfs.syncer.HttpSyncer.ORIGIN_FILENAME;
import static cn.ipman.dfs.syncer.HttpSyncer.X_FILENAME;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/7/13 08:06
 */
@RestController
public class FileController {

    @Autowired
    DfsConfigProperties properties;

    @Autowired
    HttpSyncer httpSyncer;

    @Autowired
    MQSyncer mqSyncer;

    @SneakyThrows
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         HttpServletRequest request) {

        // 1. 处理文件
        // 没有标记代表时客户端传的, 有代表是多机同步
        boolean needSync = false;
        String filename = request.getHeader(X_FILENAME);

        // 同步文件到backup
        String originalFilename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) { // upload 上传文件
            needSync = true;
            filename = getUUIDFile(originalFilename);
        } else { // 同步文件
            String xor = request.getHeader(ORIGIN_FILENAME);
            if (xor != null && !xor.isEmpty()) {
                originalFilename = xor;
            }
        }

        String subDir = FileUtils.getSubDir(filename);
        File uploadFile = new File(properties.getUploadPath() + "/" + subDir + "/" + filename);
        System.out.println(properties.getUploadPath() + "/" + subDir + "/" + filename);
        file.transferTo(uploadFile);

        // 2.处理meta
        FileMeta meta = new FileMeta();
        meta.setName(filename);
        meta.setOriginalFileName(originalFilename);
        meta.setSize(file.getSize());
        meta.setDownloadUrl(properties.getDownloadUrl());
        if (properties.isAutoMd5()) {
            meta.getTags().put("md5", DigestUtils.md5DigestAsHex(new FileInputStream(uploadFile)));
        }

        // 2.1 存放到本地文件
        String metaName = filename + ".meta";
        File metaFile = new File(properties.getUploadPath() + "/" + subDir + "/" + metaName);
        FileUtils.writeMeta(metaFile, meta);

        // 2.2 存放到数据库
        // 2.3 存放到配置或注册中心, 比如zk

        // 3.同步到backup
        // 同步文件到backup
        // 实现同步处理文件复制,也可以实现异步处理文件复制
        if (needSync) {
            if (properties.isSyncBackup()) {
                try {
                    httpSyncer.sync(uploadFile, properties.getBackupUrl(), originalFilename);
                } catch (Exception exception) {
                    System.out.println("sync error :" + exception);
                    mqSyncer.sync(meta);
                }
            } else {
                mqSyncer.sync(meta);
            }
        }
        return filename;
    }

    @RequestMapping("/download")
    public void download(String name, HttpServletResponse response) {
        String subDir = FileUtils.getSubDir(name);
        String path = properties.getUploadPath() + "/" + subDir + "/" + name;
        File file = new File(path);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[16 * 1024];

            // 加一些response的头
            response.setCharacterEncoding("UTF-8");
            response.setContentType(getMimeType(name));
            //response.setContentType("application/octet-stream");
            //response.setHeader("Content-Disposition", "attachment;filename=" + name);
            response.setHeader("Content-Length", String.valueOf(file.length()));

            // 读取文件信息, 并逐段输出
            OutputStream outputStream = response.getOutputStream();
            while (fis.read(buffer) != -1) {
                outputStream.write(buffer);
            }
            outputStream.flush();

            fis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @RequestMapping("/meta")
    public String meta(String name) {
        String subDir = FileUtils.getSubDir(name);
        String path = properties.getUploadPath() + "/" + subDir + "/" + name + ".meta";
        File metaFile = new File(path);
        try {
            return FileCopyUtils.copyToString(new FileReader(metaFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
