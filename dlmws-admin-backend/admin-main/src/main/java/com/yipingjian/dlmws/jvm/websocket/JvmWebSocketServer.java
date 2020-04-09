package com.yipingjian.dlmws.jvm.websocket;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.jvm.service.JvmService;
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
@ServerEndpoint("/ws/jvm/{ip}/{pid}")
public class JvmWebSocketServer {

    private static JvmService jvmService;

    @Autowired
    public void setJvmService(JvmService jvmService) {
        JvmWebSocketServer.jvmService = jvmService;
    }

    /**
     * 连接数
     */
    private static volatile int connCount = 0;
    /**
     * 存储 ip + pid 与ws连接映射关系的map
     */
    public static ConcurrentHashMap<String, JvmWebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与客户端的会话
     */
    private Session session;
    /**
     * the ip of the monitoring host
     */
    private String ip;
    /**
     * the pid of the monitoring jvm
     */
    private int pid;
    /**
     * the monitor time interval
     */
    public long[] time = new long[2];

    @OnOpen
    public void onOpen(Session session, @PathParam("ip") String ip, @PathParam("pid")int pid) {
        this.session = session;
        this.ip = ip;
        this.pid = pid;
        String key = ip + "," + pid;
        if(webSocketMap.containsKey(key)){
            webSocketMap.remove(key);
            webSocketMap.put(key, this);
        }else{
            webSocketMap.put(key, this);
            addOnlineCount();
        }
        sendMessage(JSONObject.toJSONString(jvmService.getJvmData(ip, pid, time)));
        log.info("{}加入, 当前在线人数为{}", key, getOnlineCount());

    }

    @OnClose
    public void onClose() {
        String key = ip + "," + pid;
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
        JSONObject jsonObject = JSONObject.parseObject(message);
        time[0] = jsonObject.getLong("st");
        time[1] = jsonObject.getLong("ed");
        sendMessage(JSONObject.toJSONString(jvmService.getJvmData(ip, pid, time)));
    }

    @OnError
    public void onError(Throwable error) {
        log.error("websocket exception ip:{}, pid:{}, {}\n{}", ip, pid, error.getMessage(), Arrays.toString(error.getStackTrace()));
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
        JvmWebSocketServer.connCount++;
    }

    public static synchronized void subOnlineCount() {
        JvmWebSocketServer.connCount--;
    }
}
