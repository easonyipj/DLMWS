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
public class logProducerB {
    @Resource
    private LogSwitchServiceImpl logSwitchService;

    @Scheduled(cron = "0/5 * * * * ? ")
    public void outOfIndexError(){
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "ipConfused"));
        if(logSwitch.getStatus()){
            log.error("ip confused");
        }
    }

    @Scheduled(cron = "0/5 * * * * ? ")
    public void badSqlError() {
        LogSwitch logSwitch = logSwitchService.getOne(new QueryWrapper<LogSwitch>().eq("name", "npeError"));
        if(logSwitch.getStatus()){
            try {
                LogSwitch logSwitch1 = null;
                logSwitch1.getStatus();
            } catch (Exception e) {
                log.error("Npe Error", e);
            }
        }
    }

}
