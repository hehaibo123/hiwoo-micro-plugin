package com.hiwoo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "micro-application.application-demo")
public class ApplicationConfig {

    private String applicationName;

    private String applicationType;

    private String applicationTag;

    private List<Resource> resources;

    private List<Menu> menus;

    @Data
    public static class Resource {

        private String resourceTag;

        private String zhCn;

        private String en;

        private List<Operate> operates;
    }

    @Data
    public static class Menu {

        private String menuTag;

        private String pluginId;

        private String zhCn;

        private String en;

        private String layout;

        private String menuType;

        private String icon;

        private String path;

        private List<Operate> operates;

        private List<Menu> menus;
    }

    @Data
    public static class Operate {
        private String operateTag;

        private String zhCn;

        private String en;
    }
}