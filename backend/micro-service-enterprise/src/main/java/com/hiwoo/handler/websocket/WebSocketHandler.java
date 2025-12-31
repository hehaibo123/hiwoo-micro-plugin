package com.hiwoo.handler.websocket;

import com.hiwoo.entity.pojo.SocketDataPOJO;
import jakarta.websocket.*;

import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/sys-socket/{topic}")
public class WebSocketHandler {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    // 连接的用户
    private static Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

    //当前发消息的人员编号
    private String topic = "";

    private static Map<String, SocketDataPOJO> socketTempDataMap = new ConcurrentHashMap<String, SocketDataPOJO>();

    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(@PathParam(value = "topic") String topic, Session session) {
        this.topic = topic;
        sessionMap.put(topic, session);
        addOnlineCount();           //在线数加1
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "topic") String topic) {
        if (sessionMap.containsKey(topic)) {
            sessionMap.remove(topic);  //从set中删除
            subOnlineCount();           //在线数减1
            log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
        }
        if (socketTempDataMap.containsKey(topic)) {
            socketTempDataMap.remove(topic);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
//        //设备
//        SocketTempData socketTempData = JSON.parseObject(message, SocketTempData.class);
//        String topic = socketTempData.getTopic();
//        socketTempDataMap.put(topic, socketTempData);
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(@PathParam(value = "topic") String topic, Session session, Throwable error) {
        log.error("发生错误:{}",error);
        if (sessionMap.containsKey(topic)) {
            sessionMap.remove(topic);
            subOnlineCount();           //在线数减1
            log.info("异常关闭！当前在线人数为" + getOnlineCount());
        }
        if (socketTempDataMap.containsKey(topic)) {
            socketTempDataMap.remove(topic);
        }
        error.printStackTrace();
    }


    public void sendMSG(String message) {
//        SysExportProgressBO exportProgressBO = JSON.parseObject(message, SysExportProgressBO.class);
//
//        String tenantId = exportProgressBO.getTenantId();
//        Long projectId = exportProgressBO.getProjectId();
//
//        for (Map.Entry<String, SocketTempData> stringSocketTempDataEntry : socketTempDataMap.entrySet()) {
//
//            String socketTopic = stringSocketTempDataEntry.getKey();
//            SocketTempData socketTempData = stringSocketTempDataEntry.getValue();
//
//            String socketType = socketTempData.getSocketType();
//            String socketTenanId = socketTempData.getTenantId().toLowerCase();
//            Long socketProjectId = socketTempData.getProjectId();
//
//            if (!StringUtils.equals(socketTenanId,tenantId)){
//                continue;
//            }
//            switch (socketType){
//                case "exportProject":
//                    if(projectId.longValue() == socketProjectId.longValue()){
//                        this.sendMsgForWeb(socketTopic, message);
//                    }
//                    break;
//            }
//        }
    }

    private void sendMsgForWeb(String topicUserId, String message) {
        try {
            if (sessionMap.containsKey(topicUserId)) {
                Session session = sessionMap.get(topicUserId);
                synchronized (session) {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketHandler.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketHandler.onlineCount--;
    }
}
