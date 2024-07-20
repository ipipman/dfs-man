package cn.ipman.dfs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${dfs.backupUrl}")
    private String backupUrl;

    @Autowired
    HttpSyncer httpSyncer;

    private String uploadPath;

    @SneakyThrows
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         HttpServletRequest request) {

        // 没有标记代表时客户端传的, 有代表是多机同步
        boolean needSync = false;
        String filename = request.getHeader(X_FILENAME);

        // 同步文件到backup
        if (filename == null || filename.isEmpty()) {
            needSync = true;
            //filename = file.getOriginalFilename();
            filename = getUUIDFile(file.getOriginalFilename());
        }

        File uploadFile = new File(uploadPath + "/" + filename);
        System.out.println(uploadPath + "/" + filename);
        file.transferTo(uploadFile);

        // 同步文件到backup
        if (needSync) {
            httpSyncer.sync(uploadFile, backupUrl);
        }

        return filename;
    }



    @RequestMapping("/download")
    public void download(String name, HttpServletResponse response) {
        String path = uploadPath + "/" + name;
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
