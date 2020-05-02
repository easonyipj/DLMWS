package com.yipingjian.dlmws.dashboard.websocket;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.dashboard.service.LogStatisticService;
import com.yipingjian.dlmws.jvm.service.JvmService;
import com.yipingjian.dlmws.jvm.websocket.JvmWebSocketServer;
import com.yipingjian.dlmws.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/jvm/{ip}/{pid}")
public class LogStatisticWebSocketServer {
    private static LogStatisticService logStatisticService;

    @Autowired
    public void setLogStatisticService(LogStatisticService logStatisticService) {
        LogStatisticWebSocketServer.logStatisticService = logStatisticService;
    }

    /**
     * 连接数
     */
    private static volatile int connCount = 0;
    /**
     * 存储 owner 与ws连接映射关系的map
     */
    public static ConcurrentHashMap<String, LogStatisticWebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与客户端的会话
     */
    private Session session;
    /**
     * the owner of the monitoring projects
     */
    private String owner;

    @OnOpen
    public void onOpen(Session session, @PathParam("owner") String owner) {
        this.owner = owner;
        this.session = session;
        if(webSocketMap.containsKey(owner)){
            webSocketMap.remove(owner);
            webSocketMap.put(owner, this);
        }else{
            webSocketMap.put(owner, this);
            addOnlineCount();
        }
        String from = DateTimeUtil.getTimestampFormat(new Date(System.currentTimeMillis() - 1000L * 24 * 3600).getTime());
        String to = DateTimeUtil.getTimestampFormat(new Date(System.currentTimeMillis()).getTime());
        sendMessage(JSONObject.toJSONString(logStatisticService.getLogStatisticSummary(owner, from, to)));
        log.info("{}加入, 当前在线人数为{}", owner, getOnlineCount());

    }

    @OnClose
    public void onClose() {
        String key = owner;
        if(webSocketMap.containsKey(key)){
            webSocketMap.remove(key);
            subOnlineCount();
        }
        log.info("{}退出, 当前在线人数为{}", key, getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     *
     * */
    @OnMessage
    public void onMessage(String message) {
        log.info("收到客户端消息:{}", message);
    }

    @OnError
    public void onError(Throwable error) {
        log.error("websocket exception owner{}\n{}", owner, Arrays.toString(error.getStackTrace()));
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message){
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("push message to client error, message:{}", message, e);
        }
    }

    public static synchronized int getOnlineCount() {
        return connCount;
    }

    public static synchronized void addOnlineCount() {
        LogStatisticWebSocketServer.connCount++;
    }

    public static synchronized void subOnlineCount() {
        LogStatisticWebSocketServer.connCount--;
    }
}
