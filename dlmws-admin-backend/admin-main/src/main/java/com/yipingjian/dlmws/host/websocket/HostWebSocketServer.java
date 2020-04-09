package com.yipingjian.dlmws.host.websocket;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.host.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/host/{ip}")
public class HostWebSocketServer {


    private static HostService hostService;

    // 注入的时候，给类的 service 注入
    @Autowired
    public void setHostService(HostService hostService) {
        HostWebSocketServer.hostService = hostService;
    }

    /**
     * 连接数
     */
    private static volatile int connCount = 0;
    /**
     * 存储ip与ws连接映射关系的map
     */
    public static ConcurrentHashMap<String, HostWebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与客户端的会话
     */
    private Session session;
    /**
     * the ip of the monitoring host
     */
    private String ip;
    /**
     * the monitor time interval
     */
    public long[] cpuInterval = new long[2];

    @OnOpen
    public void onOpen(Session session, @PathParam("ip") String ip) {
        this.session = session;
        this.ip = ip;
        if(webSocketMap.containsKey(ip)){
            webSocketMap.remove(ip);
            webSocketMap.put(ip, this);
        }else{
            webSocketMap.put(ip, this);
            addOnlineCount();
        }
        sendMessage(JSONObject.toJSONString(hostService.getHostData(ip, cpuInterval)));
        log.info("ip:{}加入, 当前在线人数为{}", ip, getOnlineCount());

    }

    @OnClose
    public void onClose() {
        if(webSocketMap.containsKey(ip)){
            webSocketMap.remove(ip);
            subOnlineCount();
        }
        log.info("ip:{}退出, 当前在线人数为{}", ip, getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     *
     * */
    @OnMessage
    public void onMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        cpuInterval[0] = jsonObject.getLong("st");
        cpuInterval[1] = jsonObject.getLong("ed");
        sendMessage(JSONObject.toJSONString(hostService.getHostData(ip, cpuInterval)));
    }

    @OnError
    public void onError(Throwable error) {
        log.error("websocket exception ip:{}, {}\n{}", ip, error.getMessage(), Arrays.toString(error.getStackTrace()));
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
        HostWebSocketServer.connCount++;
    }

    public static synchronized void subOnlineCount() {
        HostWebSocketServer.connCount--;
    }
}
