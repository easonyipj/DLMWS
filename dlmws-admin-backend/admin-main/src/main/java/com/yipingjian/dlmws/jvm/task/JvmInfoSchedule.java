package com.yipingjian.dlmws.jvm.task;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.jvm.service.JvmService;
import com.yipingjian.dlmws.jvm.websocket.JvmWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JvmInfoSchedule {

    @Resource
    private JvmService jvmService;

    @Scheduled(cron = "0/5 * * * * ? ")
    public void jvmInfo() {
        // TODO 用线程池优化
        ConcurrentHashMap<String, JvmWebSocketServer> webSocketMap = JvmWebSocketServer.webSocketMap;
        for(Map.Entry<String, JvmWebSocketServer> entry : webSocketMap.entrySet()) {
            String[] keys = entry.getKey().split(",");
            String ip = keys[0];
            int pid = Integer.parseInt(keys[1]);
            JvmWebSocketServer jvmWebSocketServer = entry.getValue();
            jvmWebSocketServer.sendMessage(JSONObject.toJSONString(jvmService.getJvmData(ip, pid, jvmWebSocketServer.time)));
        }

    }
}
