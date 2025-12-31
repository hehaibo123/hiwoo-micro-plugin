package com.hiwoo.entity.po;

import lombok.Data;

/**
 *
 * @Author Haibo He
 * @Date 2023/9/5 19:48
 * @Version 1.0
 */
@Data
public class SysResourceOperate {

    private static final long serialVersionUID = 1L;

    private String applicationTag;

    private String resourceTag;

    private String operateTag;

    private String zhCn;

    private String en;
}
