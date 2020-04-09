package com.yipingjian.dlmws.host.task;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.host.service.HostService;
import com.yipingjian.dlmws.host.websocket.HostWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HostInfoSchedule {

    @Resource
    private HostService hostService;

    @Scheduled(cron = "0/5 * * * * ? ")
    public void hostInfo() {
        // TODO 用线程池优化
        ConcurrentHashMap<String, HostWebSocketServer> webSocketMap = HostWebSocketServer.webSocketMap;
        for(Map.Entry<String, HostWebSocketServer> entry : webSocketMap.entrySet()) {
            String ip = entry.getKey();
            HostWebSocketServer hostWebSocketServer = entry.getValue();
            hostWebSocketServer.sendMessage(JSONObject.toJSONString(hostService.getHostData(ip, hostWebSocketServer.cpuInterval)));
        }
    }
}
