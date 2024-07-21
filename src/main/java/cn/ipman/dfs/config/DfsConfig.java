package cn.ipman.dfs.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

/**
 * 该类用于在应用中配置文件上传设置。
 *
 * @Author IpMan
 * @Date 2024/7/13 10:03
 */
public class DfsConfig {

    /**
     * 创建并配置MultipartConfigElement Bean。
     * 此配置用于指定临时存储上传文件的位置，
     * 以及与文件上传相关的其他设置。
     *
     * @return MultipartConfigElement 配置好的多部分配置元素
     */
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation("/private/tmp/tomcat");
        return factory.createMultipartConfig();
    }
}
