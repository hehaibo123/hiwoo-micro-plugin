package com.hiwoo.entity.constant;


import lombok.Data;
import lombok.experimental.UtilityClass;

/**
 * MQTT 常量
 * + 代表单一匹配  # 代表多级匹配
 */
@UtilityClass
@Data
public class MQTTConstant {
    public static String CONVERT_REAL_TIME_DATA = "/translate/+/realtime";   //转换实时数据

    public static String ANALOG_REAL_TIME_DATA = "/analog/+/realtime";  //模拟实时数据

    public static String ANALOG_HISTORY_DATA = "/analog/+/history";  //模拟历史数据

    public static String CLEAN_REAL_TIME_DATA = "/clean/+/realtime";  //清洗实时数据

    public static String CLEAN_HISTORY_DATA = "/clean/+/history";  //清洗历史数据

    public static String CLEAN_WARN_DATA = "/clean/+/warn";  //清洗报警数据

    public static String CLEAN_WARN_Gateway_DATA = "/clean/+/gatewaywarn";  //清洗网关报警数据


    public static String PRIMITIVE_CONTROL_DATA = "/platform/+/control/request";  //清洗后控制数据

    public static String CONTROL_HANDLER = "/clean/+/control";  //原始控制数据

    public static String REAL_NOTIFY_STATUS_DATA = "/+/real/notify/status"; //设备状态

    public static String REAL_DATA_DEFAULT_DATA = "/+/real/data/default"; //实时数据

    // .... 后续在这里补充 TODO

    //与BoxPlugin交互主题
    public static String BOX_INFO_DATA = "/box/info/+/+"; //接收BoxPlugin基本信息同步

    public static String BOX_CONFIG_DATA = "/box/config/+/+"; //接收BoxPlugin配置信息

    public static String REAL_LOCATION_DATA = "/+/real/location"; //实时地址信息

    public static String WRITE_DATA_BOX = "/commService/boxDatas/req/+"; //网关反向控制

    public static String REAL_NOTIY_ALARM = "/+/real/notify/alarm"; //网关实时报警主题

    public static String RECEIV_BOXPLUGIN_USER = "/engine/user"; //接收BoxPlugin推送来的用户信息

    public static String SEND_USER_TO_BOXPLUGIN = "/boxplugin/user"; //将用户信息推送至BoxPlugin

    public static String MANGER_LAST_WILL = "/+/willmessage"; //网关遗嘱消息 管理主题:MangerLastWill

    public static String DYNAMIC_DEVICE = "/+/dynamic/devices"; //动态设备主题

    public static String DYNAMIC_DEVICE_LOCATION = "/device/location/+";

    public static String REAL_NOTIY_GATEWAY_ALARM = "/+/real/notify/gatewayalarm"; //网关实时报警主题

    // .... 后续在这里补充 TODO

    public static String SEND_DELETE_ORG = "/delete/org"; //删除组织机构

    public static String SEND_REGISTER_APPLICATION = "/register/application/+"; //应用/插件注册

    public static String SEND_REGISTER_RESOURCE = "/register/application/resource/+"; //应用/插件资源注册

    public static String SEND_EXPORT_RESOURCE = "/export/application/resource/+"; //应用/插件资源导出

    public static String SEND_IMPORT_RESOURCE = "/import/application/resource/+"; //应用/插件资源导入

    public static String SEND_EXPORT_HANDLER_STATUS = "/export/handle/status/+"; //应用/插件资源导出状态

    public static String SEND_IMPORT_HANDLER_STATUS = "/import/handle/status/+"; //应用/插件资源导入状态

    public static String SEND_SYNC_NOTICE = "/sync/notice/+"; //应用/插件资源通知同步


    public static String analogRealtimeData(String dataId) {
        return ANALOG_REAL_TIME_DATA.replace("+", dataId);
    }

    public static String cleanRealTimeData(String dataId) {
        return CLEAN_REAL_TIME_DATA.replace("+", dataId);
    }

    public static String analogHistoryData(String dataId) {
        return ANALOG_HISTORY_DATA.replace("+", dataId);
    }

    public static String cleanHistoryData(String dataId) {
        return CLEAN_HISTORY_DATA.replace("+", dataId);
    }

    public static String writeBoxData(String dataId) {
        return WRITE_DATA_BOX.replace("+", dataId);
    }

    public static String writeControlData(String dataId) {
        return PRIMITIVE_CONTROL_DATA.replace("+", dataId);
    }

    public static String dynamicLocation(String dataId) {
        return DYNAMIC_DEVICE_LOCATION.replace("+", dataId);
    }

    public static String registerApplication(String application) {
        return SEND_REGISTER_APPLICATION.replace("+", application);
    }

    public static String registerResource(String application) {
        return SEND_REGISTER_RESOURCE.replace("+", application);
    }

    public static String exportProject(String projectId) {
        return SEND_EXPORT_RESOURCE.replace("+", projectId);
    }

    public static String importProject(String projectId) {
        return SEND_IMPORT_RESOURCE.replace("+", projectId);
    }

    public static String exportProjectStatus(String projectId) {
        return SEND_EXPORT_HANDLER_STATUS.replace("+", projectId);
    }

    public static String importProjectStatus(String projectId) {
        return SEND_IMPORT_HANDLER_STATUS.replace("+", projectId);
    }
}


