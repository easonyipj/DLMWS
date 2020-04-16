package com.yipingjian.dlmws.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yipingjian.dlmws.entity.LogSwitch;
import com.yipingjian.dlmws.service.impl.LogSwitchServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class logProducerC {
    @Resource
    private LogSwitchServiceImpl logSwitchService;

    @Scheduled(cron = "0/10 * * * * ? ")
    public void outOfIndexError(){
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "diyErrorLog"));
        LogSwitch forSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "10Counts"));
        if(logSwitch.getStatus()){
            if(forSwitch.getStatus()) {
                for(int i = 0 ; i < 10; i++) {
                    log.error(logSwitch.getMessage());
                }
            }else {
                log.error(logSwitch.getMessage());
            }
        }
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void badSqlError() {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "diyWarnLog"));
        LogSwitch forSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "100Counts"));
        if(logSwitch.getStatus()){
            if(forSwitch.getStatus()) {
                for(int i = 0 ; i < 100; i++) {
                    log.warn(logSwitch.getMessage());
                }
            }else {
                log.warn(logSwitch.getMessage());
            }
        }
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void infoDiy() {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "diyInfoLog"));
        LogSwitch forSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "1000Counts"));
        if(logSwitch.getStatus()){
            if(forSwitch.getStatus()) {
                for(int i = 0 ; i < 1000; i++) {
                    log.info(logSwitch.getMessage());
                }
            }else {
                log.info(logSwitch.getMessage());
            }
        }
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void debugDiy() {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "diyDebugLog"));
        log.debug(logSwitch.getMessage());
    }
}
