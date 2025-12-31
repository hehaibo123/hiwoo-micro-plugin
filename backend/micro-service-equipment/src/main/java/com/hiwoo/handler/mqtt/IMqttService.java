package com.hiwoo.handler.mqtt;


import com.hiwoo.entity.enums.QoSEnum;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;

public interface IMqttService {

    MqttClient createConn(MqttCallback mqttCallback);
    /**
     * 订阅方法
     * @param topic
     * @param qos
     * @return
     */
    Boolean subscribe(String topic, QoSEnum qos);

    /**
     * 批量订阅
     * @param topicArr
     * @param qosArr
     * @return
     */
    Boolean subscribes(String[] topicArr, int[] qosArr);

    /**
     * 订阅方法2, qos 默认为0
     * @param topic
     * @return
     */
    Boolean subscribe(String topic);

    /**
     * 订阅带回调
     * @param topic
     * @param qos
     * @param iMqttMessageListener
     * @return
     */
    Boolean subscribe(String topic, QoSEnum qos, IMqttMessageListener iMqttMessageListener);

    /**
     * 取消订阅
     * @param topic
     * @return
     */
    Boolean unSubscribe(String topic);

    /**
     * 批量取消定义
     * @param topics
     * @return
     */
    Boolean unSubscribe(String[] topics);


    /**
     * 发布保留消息
     * @param topic
     * @param qos
     * @param content
     * @param retained
     * @return
     */
    Boolean publish(String topic, QoSEnum qos, String content, Boolean retained);

    /**
     * 发布消息方法
     * @param topic
     * @param qos
     * @param content
     * @return
     */
    Boolean publish(String topic, QoSEnum qos, String content);

    /**
     * 发布消息，qos为0
     * @param topic
     * @param content
     * @return
     */
    Boolean publish(String topic, String content);

    /**
     * 重新订阅
     */
    void reSubscribe();

    Boolean subscriberPublicTopic();
}
