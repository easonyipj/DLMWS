package com.yipingjian.dlmws.java.task;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.java.entity.JPS;
import com.yipingjian.dlmws.java.service.JavaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JavaMonitorTask {

    @Resource
    private JavaService javaService;

    private static List<Integer> pidList = Lists.newArrayList();

    /**
     * 每10s更新一次pid List
     */
    @Scheduled(cron = "*/10 * * * * ? ")
    public void updatePidList() {
        try {
            pidList = javaService.getJPSInfo().stream().map(JPS::getPid).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("update pid list error", e);
        }
    }

    @Scheduled(cron = "*/3 * * * * ? ")
    public void getJVMMemory() {
        for (Integer pid : pidList) {
            try {
                log.info(javaService.getJVMMemoryInfo(pid).toString());
            } catch (Exception e) {
                log.error("getJVMMemory error, pid:{}", pid, e);
            }
        }
    }

    @Scheduled(cron = "*/3 * * * * ? ")
    public void getJVMClass() {
        for (Integer pid : pidList) {
            try {
                log.info(javaService.getJVMClassInfo(pid).toString());
            } catch (Exception e) {
                log.error("getJVMClass error, pid:{}", pid, e);
            }
        }
    }

    @Scheduled(cron = "*/3 * * * * ? ")
    public void getJVMThread() {
        for (Integer pid : pidList) {
            try {
                log.info(javaService.getJVMThreadInfo(pid).toString());
            } catch (Exception e) {
                log.error("getJVMThread error, pid:{}", pid, e);
            }
        }
    }
}
