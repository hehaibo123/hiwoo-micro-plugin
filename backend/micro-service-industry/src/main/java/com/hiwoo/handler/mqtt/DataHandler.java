package com.hiwoo.handler.mqtt;

import com.hiwoo.entity.constant.MQTTConstant;
import com.hiwoo.handler.websocket.WebSocketHandler;
import com.hiwoo.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


//每次获取bean都会获得一个新的对象
@Scope("prototype")
@Slf4j
@Component
public class DataHandler {
    @Resource
    private WebSocketHandler webSocketHandler;

    @Async
    public void subscribeCallback(String topic, String mqttMsgData) {
        try {
            if (StringUtils.isNotEmpty(mqttMsgData)) {
                if (topic.startsWith(MQTTConstant.registerResource(""))) {

                }
            }
        } catch (Exception ex) {
            log.error("MQTT消息处理故障，故障信息为：" + ex);
            ex.printStackTrace();
        }
    }
}
