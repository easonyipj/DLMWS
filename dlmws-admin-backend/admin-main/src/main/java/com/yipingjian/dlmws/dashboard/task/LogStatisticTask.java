package com.yipingjian.dlmws.dashboard.task;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.dashboard.entity.LogStatistic;
import com.yipingjian.dlmws.dashboard.service.LogStatisticService;
import com.yipingjian.dlmws.dashboard.websocket.LogStatisticWebSocketServer;
import com.yipingjian.dlmws.host.websocket.HostWebSocketServer;
import com.yipingjian.dlmws.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LogStatisticTask {

    @Resource
    private LogStatisticService logStatisticService;

    @Scheduled(cron = "0/5 * * * * ? ")
    public void pushLogStatisticMessage() {
        ConcurrentHashMap<String, LogStatisticWebSocketServer> concurrentHashMap = LogStatisticWebSocketServer.webSocketMap;
        for(Map.Entry<String, LogStatisticWebSocketServer> entry : concurrentHashMap.entrySet()) {
            String owner = entry.getKey();
            String from = DateTimeUtil.getTimestampFormat(new Date(System.currentTimeMillis() - 1000L * 24 * 3600).getTime());
            String to = DateTimeUtil.getTimestampFormat(new Date(System.currentTimeMillis()).getTime());
            entry.getValue().sendMessage(JSONObject.toJSONString(logStatisticService.getLogStatisticSummary(owner, from, to)));
        }
    }
}
