package com.hiwoo.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author Haibo He
 * @Date 2023/9/5 19:48
 * @Version 1.0
 */
@Data
@TableName("sys_menu_operate")
public class SysMenuOperate {

    private static final long serialVersionUID = 1L;

    private String applicationTag;

    private String menuTag;

    private String operateTag;

    private String zhCn;

    private String en;
}
