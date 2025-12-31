package com.hiwoo.handler.mqtt;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PushCallback implements MqttCallbackExtended {

    @Resource
    private DataHandler handler;

    @Resource
    private IMqttService mqttService;

    @Override
    public void connectionLost(Throwable throwable) {

        log.error("connection lost:{}" , throwable.getMessage());

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

        String data = new String(mqttMessage.getPayload());
        handler.subscribeCallback(topic, data);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("deliveryComplete:{}", iMqttDeliveryToken.isComplete());
    }

    @Override
    public void connectComplete(boolean b, String s) {
        log.info("connectComplete:{},{}", s + "已连接", "主题订阅完成");
        mqttService.subscriberPublicTopic();
    }
}
