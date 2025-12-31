package com.hiwoo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {

    private String host;
    private String username;
    private String password;
    private Integer connectionTimeout;
    private Integer keepAliveInterval;
    private String publishClientId;
    private String subscribeClientId;
    private String[] hosts;
}

