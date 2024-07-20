package cn.ipman.dfs;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/7/20 17:38
 */
@Component
public class MQSyncer {

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    private String topic = "dfs-man";

    public void sync(FileMeta meta) {
        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(meta)).build();
        rocketMQTemplate.send(topic, message);
        System.out.println(" ===> send message: " + message);
    }

    
}
