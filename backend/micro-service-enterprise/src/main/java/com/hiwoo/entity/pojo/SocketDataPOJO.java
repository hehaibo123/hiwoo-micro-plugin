package com.hiwoo.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装Socket链接信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketDataPOJO {

    /**
     * Socket主题
     */
    private String topic;

    /**
     * socket类型
     */
    private String socketType;

    /**
     * 租户
     */
    private String tenantId;
    /**
     * 项目标识
     */
    private Long projectId;

}
