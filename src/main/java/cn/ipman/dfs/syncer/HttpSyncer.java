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
 * sync file to backup server.
 *
 * @Author IpMan
 * @Date 2024/7/13 09:33
 */
@Component
public class HttpSyncer {

    public final static String X_FILENAME = "X-Filename";
    public final static String ORIGIN_FILENAME = "X-Orig-Filename";

    public String sync(File file, String url, String originalFilename) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add(X_FILENAME, file.getName());
        headers.add(ORIGIN_FILENAME, originalFilename);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity
                = new HttpEntity<>(builder.build(), headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        String result = responseEntity.getBody();
        System.out.println("sync result = " + result);
        return result;
    }

}

