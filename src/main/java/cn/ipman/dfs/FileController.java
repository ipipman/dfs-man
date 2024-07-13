package cn.ipman.dfs;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

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

    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String filename = file.getOriginalFilename();
        File uploadFile = new File(uploadPath + "/" + filename);
        System.out.println(uploadPath + "/" + filename);
        file.transferTo(uploadFile);
        return filename;
    }


}
