package com.hiwoo.handler.mqtt;

import com.alibaba.fastjson2.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.hiwoo.config.ApplicationConfig;
import com.hiwoo.config.MqttConfig;
import com.hiwoo.entity.constant.MQTTConstant;
import com.hiwoo.entity.enums.QoSEnum;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MqttServiceImpl implements IMqttService {

    private static final int MAX_RECONNECT_DELAY = 60000; // 最大重连延迟
    private static final int INITIAL_RECONNECT_DELAY = 1000; // 初始重连延迟

    private MqttClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;
    private int currentReconnectDelay = INITIAL_RECONNECT_DELAY;
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean isConnecting = false;

    @Autowired
    private ApplicationConfig sysApplicationConfig;

    @Resource
    private MqttConfig sysMqttConfig;

    @Lazy
    @Resource
    private PushCallback sysPushCallback;

    @Resource
    private ThreadPoolExecutor mqttMessageExecutor;

    // 添加消息处理限流器
    private final RateLimiter messageRateLimiter = RateLimiter.create(1000.0); // 每秒1000条消息

    //    @PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 异步初始化MQTT连接
        mqttMessageExecutor.execute(() -> {
            try {
                createConn();
            } catch (Exception e) {
                log.error("MQTT initialization failed", e);
            }
        });
    }

    public MqttClient createConn() {
        return this.createConn(sysPushCallback);
    }

    @Override
    public MqttClient createConn(MqttCallback mqttCallback) {
        if (isConnecting) {
            log.warn("MQTT connection is already in progress");
            return mqttClient;
        }

        try {
            isConnecting = true;
            log.info("MQTT连接服务器中...");

            String clientId = "system" + sysMqttConfig.getSubscribeClientId() + "_" + UUID.randomUUID().toString() + (new Date()).getTime();
            mqttClient = new MqttClient(sysMqttConfig.getHost(), clientId, new MemoryPersistence());

            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(false);
            mqttConnectOptions.setUserName(sysMqttConfig.getUsername());
            mqttConnectOptions.setPassword(sysMqttConfig.getPassword().toCharArray());
            mqttConnectOptions.setConnectionTimeout(sysMqttConfig.getConnectionTimeout());
            mqttConnectOptions.setKeepAliveInterval(sysMqttConfig.getKeepAliveInterval());
            mqttConnectOptions.setAutomaticReconnect(true);

            if (sysMqttConfig.getHosts() != null && sysMqttConfig.getHosts().length > 0) {
                mqttConnectOptions.setServerURIs(sysMqttConfig.getHosts());
            }

            mqttClient.setCallback(mqttCallback);
            IMqttToken iMqttToken = mqttClient.connectWithResult(mqttConnectOptions);
            log.info("MQTT连接服务器成功{iMqttToken}", iMqttToken);

            // 连接成功后重置重连延迟
            currentReconnectDelay = INITIAL_RECONNECT_DELAY;

            this.publish(MQTTConstant.registerApplication(sysApplicationConfig.getApplicationTag()), QoSEnum.AT_LEAST_ONCE, JSON.toJSONString(sysApplicationConfig)); // 推数据

            return mqttClient;

        } catch (Exception e) {
            log.error("MQTT连接服务器失败...", e);
            handleConnectionFailure();
            return null;
        } finally {
            isConnecting = false;
        }
    }

    private void handleConnectionFailure() {
        if (mqttClient != null) {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                log.error("Error disconnecting MQTT client", e);
            }
        }
        reConnect();
    }

    public void reConnect() {
        reconnectExecutor.schedule(() -> {
            try {
                if (createConn() != null) {
                    log.info("MQTT reconnection successful");
                    return;
                }
            } catch (Exception e) {
                log.error("MQTT reconnection failed", e);
            }
            currentReconnectDelay = Math.min(currentReconnectDelay * 2, MAX_RECONNECT_DELAY);
            reConnect();
        }, currentReconnectDelay, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
            reconnectExecutor.shutdown();
            if (!reconnectExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                reconnectExecutor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Error during MQTT shutdown", e);
        }
    }

    @Override
    public Boolean subscribe(String topic, QoSEnum qos) {

        try {
            // 判定是否需要重新连接
            if (null != mqttClient && mqttClient.isConnected()) {
                mqttClient.subscribe(topic, qos.val);
            }

        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean subscribes(String[] topicArr, int[] qosArr) {
        try {

            if (null != mqttClient && mqttClient.isConnected()) {
                mqttClient.subscribe(topicArr, qosArr);
            } else {
                log.error("Mqtt client is disConnected");
            }

        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean subscribe(String topic) {
        return this.subscribe(topic, QoSEnum.AT_MOST_ONCE);
    }

    @Override
    public Boolean subscribe(String topic, QoSEnum qos, IMqttMessageListener iMqttMessageListener) {
        try {

            if (null != mqttClient && mqttClient.isConnected()) {

                IMqttToken token = mqttClient.subscribeWithResponse(topic, qos.val, iMqttMessageListener);
                log.info("-----------");
            } else {
                log.error("Mqtt client is disConnected");
            }
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean unSubscribe(String topic) {
        try {
            if (mqttClient == null || (null != mqttClient && !mqttClient.isConnected())) {
                mqttClient.unsubscribe(topic);
            }
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean unSubscribe(String[] topics) {
        try {
            if (mqttClient == null || (null != mqttClient && !mqttClient.isConnected())) {
                mqttClient.unsubscribe(topics);
            }
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean publish(String topic, QoSEnum qos, String content, Boolean retained) {
        if (!messageRateLimiter.tryAcquire(100, TimeUnit.MILLISECONDS)) {
            log.warn("Message rate limit exceeded for topic: {}", topic);
            return false;
        }

        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos.val);
                message.setRetained(retained);
                mqttClient.publish(topic, message);
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to publish message to topic: {}", topic, e);
        }
        return false;
    }

    @Override
    public Boolean publish(String topic, QoSEnum qos, String content) {
        return this.publish(topic, qos, content, false);
    }

    @Override
    public Boolean publish(String topic, String content) {
        return this.publish(topic, QoSEnum.AT_MOST_ONCE, content, false);
    }

    @Override
    public void reSubscribe() {
        this.subscriberPublicTopic();
    }

    @Override
    public Boolean subscriberPublicTopic(){
        try {
            String[] topicArr = {
                    MQTTConstant.RECEIV_BOXPLUGIN_USER,
                    MQTTConstant.SEND_REGISTER_APPLICATION,
                    MQTTConstant.SEND_REGISTER_RESOURCE,
                    MQTTConstant.SEND_SYNC_NOTICE,
                    MQTTConstant.SEND_EXPORT_RESOURCE,
                    MQTTConstant.SEND_IMPORT_RESOURCE,
                    MQTTConstant.SEND_EXPORT_HANDLER_STATUS,
                    MQTTConstant.SEND_IMPORT_HANDLER_STATUS,
            };
            int[] qosArr = {
                    QoSEnum.AT_LEAST_ONCE.val,
                    QoSEnum.AT_LEAST_ONCE.val,
                    QoSEnum.AT_LEAST_ONCE.val,
                    QoSEnum.AT_LEAST_ONCE.val,
                    QoSEnum.AT_LEAST_ONCE.val,
                    QoSEnum.AT_LEAST_ONCE.val,
                    QoSEnum.AT_LEAST_ONCE.val,
                    QoSEnum.AT_LEAST_ONCE.val
            };
            this.subscribes(topicArr, qosArr);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

    }

}
