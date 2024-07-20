package cn.ipman.dfs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

import static cn.ipman.dfs.FileUtils.getMimeType;
import static cn.ipman.dfs.FileUtils.getUUIDFile;
import static cn.ipman.dfs.HttpSyncer.X_FILENAME;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/7/13 08:06
 */
@RestController
public class FileController {

    @Value("${dfs.path}")
    private String uploadPath;

    @Value("${dfs.backupUrl}")
    private String backupUrl;

    @Autowired
    HttpSyncer httpSyncer;

    @Value("${dfs.autoMd5}")
    private boolean autoMd5;

    @SneakyThrows
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         HttpServletRequest request) {

        // 1. 处理文件
        // 没有标记代表时客户端传的, 有代表是多机同步
        boolean needSync = false;
        String filename = request.getHeader(X_FILENAME);

        // 同步文件到backup
        if (filename == null || filename.isEmpty()) {
            needSync = true;
            //filename = file.getOriginalFilename();
            filename = getUUIDFile(file.getOriginalFilename());
        }

        String subDir = FileUtils.getSubDir(filename);
        File uploadFile = new File(uploadPath + "/" + subDir + "/" + filename);
        System.out.println(uploadPath + "/" + subDir + "/" + filename);
        file.transferTo(uploadFile);

        // 2.处理meta
        FileMeta meta = new FileMeta();
        meta.setName(filename);
        meta.setOriginalFileName(file.getOriginalFilename());
        meta.setSize(file.getSize());
        if (autoMd5) {
            meta.getTags().put("md5", DigestUtils.md5DigestAsHex(new FileInputStream(uploadFile)));
        }

        // 2.1 存放到本地文件
        String metaName = filename + ".meta";
        File metaFile = new File(uploadPath + "/" + subDir + "/" + metaName);
        FileUtils.writeMeta(metaFile, meta);

        // 2.2 存放到数据库
        // 2.3 存放到配置或注册中心,比如zk


        // 3.同步到backup
        // 同步文件到backup
        if (needSync) {
            httpSyncer.sync(uploadFile, backupUrl);
        }

        return filename;
    }


    @RequestMapping("/download")
    public void download(String name, HttpServletResponse response) {
        String subDir = FileUtils.getSubDir(name);
        String path = uploadPath + "/" + subDir + "/" + name;
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


}
