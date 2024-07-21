package cn.ipman.dfs;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


import static cn.ipman.dfs.utils.FileUtils.init;

@SpringBootApplication
@Import(RocketMQAutoConfiguration.class)
public class DfsManApplication {

    public static void main(String[] args) {
        SpringApplication.run(DfsManApplication.class, args);
    }


    // 1. 基于文件存储的分布式文件系统
    // 2. 块存储 ==》 最常见,效果最高
    // 3. 对象存储


    @Value("${dfs.path}")
    private String uploadPath;

    @Bean
    ApplicationRunner runner() {
        return args -> {
            init(uploadPath);
            System.out.println("dfs started");
        };
    }
}
