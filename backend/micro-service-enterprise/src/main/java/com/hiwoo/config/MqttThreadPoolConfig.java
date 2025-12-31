package com.hiwoo.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MqttThreadPoolConfig {
    
    @Bean
    public ThreadPoolExecutor mqttMessageExecutor() {
        return new ThreadPoolExecutor(
            10, // 核心线程数
            20, // 最大线程数
            60L, // 空闲线程存活时间
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), // 工作队列
            new ThreadFactoryBuilder().setNameFormat("mqtt-message-handler-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy() // 满载策略
        );
    }
} 