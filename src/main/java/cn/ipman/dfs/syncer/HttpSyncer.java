package cn.ipman.dfs.syncer;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import org.springframework.http.HttpHeaders;

/**
 * 用于通过HTTP同步文件到备份服务器的组件类。
 * 使用Spring的RestTemplate执行文件上传操作。
 *
 * @Author IpMan
 * @Date 2024/7/13 09:33
 */
@Component
public class HttpSyncer {

    /**
     * 自定义HTTP头名称：文件名。
     */
    public final static String X_FILENAME = "X-Filename";

    /**
     * 自定义HTTP头名称：原始文件名。
     */
    public final static String ORIGIN_FILENAME = "X-Orig-Filename";

    /**
     * 同步文件至指定URL。
     *
     * @param file 要同步的文件。
     * @param url 文件同步的目标URL。
     * @param originalFilename 原始文件名，用于设置自定义HTTP头。
     */
    public void sync(File file, String url, String originalFilename) {
        // 创建RestTemplate实例。
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add(X_FILENAME, file.getName());
        headers.add(ORIGIN_FILENAME, originalFilename);

        // 使用MultipartBodyBuilder构建请求体，上传文件。
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file));

        // 将构建好的请求体和头转换为HttpEntity
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity
                = new HttpEntity<>(builder.build(), headers);

        // 执行POST请求并获取响应实体
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        String result = responseEntity.getBody();
        System.out.println("sync result = " + result);
    }

}

