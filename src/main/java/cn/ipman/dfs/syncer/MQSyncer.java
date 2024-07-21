package cn.ipman.dfs.syncer;

import cn.ipman.dfs.meta.FileMeta;
import cn.ipman.dfs.utils.FileUtils;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/7/20 17:38
 */
@Component
public class MQSyncer {

    @Value("${dfs.path}")
    private String uploadPath;

    @Value("${dfs.group}")
    private String group;

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Value("${dfs.downloadUrl}")
    private String localUrl;

    String topic = "dfs-man";

    public void sync(FileMeta meta) {
        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(meta)).build();
        rocketMQTemplate.send(topic, message);
        System.out.println(" ===> send message: " + message);
    }


    @Service
    @RocketMQMessageListener(topic = "dfs-man", consumerGroup = "${dfs.group}")
    public class FileMQSyncer implements RocketMQListener<MessageExt> {
        @Override
        public void onMessage(MessageExt message) {
            // 1. 从消息里拿到meta数据
            System.out.println(" ===> onMessage ID: " + message.getMsgId());
            String json = new String(message.getBody());
            System.out.println(" ===> message json = " + json);
            FileMeta meta = JSON.parseObject(json, FileMeta.class);
            String downloadUrl = meta.getDownloadUrl();
            if (downloadUrl == null || downloadUrl.isEmpty()) {
                System.out.println(" ===> downloadUrl is empty.");
                return;
            }

            // 去重当前机器
            if (localUrl.equals(downloadUrl)) {
                System.out.println("@@@@@@@@ => the same file server, ignore mq sync task.");
                return;
            }
            System.out.println("@@@@@@@@ => the other file server, process mq sync task.");

            // 2. 写meta文件
            String dir = uploadPath + "/" + meta.getName().substring(0, 2);
            File metaFile = new File(dir + "/" + meta.getName() + ".meta");
            if (metaFile.exists()) {
                System.out.println(" ===> meta file exists and ignore save: " + metaFile.getAbsoluteFile());
            } else {
                System.out.println(" ===> meta file save: " + metaFile.getAbsoluteFile());
                FileUtils.writeString(metaFile, json);
            }

            // 3.下载文件
            File file = new File(dir, meta.getName());
            if (file.exists() && file.length() == meta.getSize()) {
                System.out.println(" ===> file exists and ignore download: " + file.getAbsoluteFile());
                return;
            }

            String download = downloadUrl + "?name=" + file.getName();
            FileUtils.download(download, file);
        }
    }

}
