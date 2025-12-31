/*
 * Copyright 2020-2030, MateCloud, DAOTIANDI Technology Inc All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Author: pangu(7333791@qq.com)
 */
package com.hiwoo.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//import jakarta.persistence.Column;

/**
 * @Author Haibo He
 * @Date 2023/9/5 19:48
 * @Version 1.0
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String pluginId;

    private Integer pid;

    private String path;

    private String entry;

    private String activeRule;

    private String menuType;

    private String title;

    private String icon;

    private boolean isLink;

    private boolean isHide;

    private boolean isKeepAlive;

    private boolean isAffix;

    private boolean isIframe;

    private Integer isDiy;

    //菜单排序
    private Integer menuOrder;

    private String zhCn;

    private String en;

    private String layout;

    private String systemType;

    private String tenantId;

    private String applicationTag;

    private String menuTag;

}
