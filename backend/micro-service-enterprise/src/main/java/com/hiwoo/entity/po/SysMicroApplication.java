package com.hiwoo.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * @Author Haibo He
 * @Date 2023/9/5 19:48
 * @Version 1.0
 */
@Data
@TableName("sys_micro_application")
public class SysMicroApplication {

    private static final long serialVersionUID = 1L;

    @TableId(value = "application_tag", type = IdType.INPUT)
    private String applicationTag;

    private String applicationName;

    private String applicationType;
}
