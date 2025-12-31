package com.hiwoo.entity.bo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MicroApplicationMenuBO {

    private String menuTag;

    private String pluginId;

    private String zhCn;

    private String en;

    private String layout;

    private String menuType;

    private String icon;

    private String path;

    private List<OperateBO> operates;

    private List<MicroApplicationMenuBO> menus;
}
