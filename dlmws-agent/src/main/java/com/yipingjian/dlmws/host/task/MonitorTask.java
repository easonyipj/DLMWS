package com.yipingjian.dlmws.host.task;

import com.yipingjian.dlmws.host.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@EnableScheduling
@Component
@Slf4j
public class MonitorTask {

    @Resource
    private HostService hostService;

    @Scheduled(cron = "*/5 * * * * ? ")
    public void getCPUInfo() {
        log.info(hostService.getCPULoad().toString());
    }
}
