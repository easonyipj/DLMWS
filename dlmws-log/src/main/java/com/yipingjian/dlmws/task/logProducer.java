package com.yipingjian.dlmws.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yipingjian.dlmws.entity.LogSwitch;
import com.yipingjian.dlmws.service.impl.LogSwitchServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Component
public class logProducer {

    @Resource
    private LogSwitchServiceImpl logSwitchService;

    @Scheduled(cron = "0/20 * * * * ? ")
    public void outOfIndexError(){
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "outOfIndexError"));
        if(logSwitch.getStatus()){
            try {
                int[] array = new int[3];
                array[1] = 1;
                int a = array[3];
            } catch (Exception e) {
                log.error(" index out of bound", e);
            }
        }
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void badSqlError() {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "badSqlError"));
        if(logSwitch.getStatus()){
            try {
                logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("badSqlError", "badSqlError"));
            } catch (Exception e) {
                log.error(" bad sql error", e);
            }
        }
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void diyError() {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "diyError"));
        if(logSwitch.getStatus()){
            log.error("something unexpected happened");
        }
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void systemInfo() {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "systemInfo"));
        if(logSwitch.getStatus()){
            log.info("the sysTem info is...");
        }
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void ipInfo() throws UnknownHostException {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "ipInfo"));
        if(logSwitch.getStatus()){
            log.info("ip of the host is:{}", InetAddress.getLocalHost().getHostAddress());
        }
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void sqlDebug() {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "sqlDebug"));
        if(logSwitch.getStatus()){
            log.debug("sqlDebug");
        }
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void warning() {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "warning"));
        if(logSwitch.getStatus()){
            log.warn("warning log example");
        }
    }

}
