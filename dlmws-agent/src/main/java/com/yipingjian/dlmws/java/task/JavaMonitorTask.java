package com.yipingjian.dlmws.java.task;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.common.utils.CommonUtils;
import com.yipingjian.dlmws.java.entity.JPS;
import com.yipingjian.dlmws.java.service.JavaService;
import com.yipingjian.dlmws.kafka.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JavaMonitorTask {

    @Resource
    private JavaService javaService;
    @Resource
    private KafkaProducer kafkaProducer;

    private static List<Integer> pidList = Lists.newArrayList();
    private static final String JVM_MEM = "jvm-mem";
    private static final String JVM_THREAD = "jvm-thread";
    private static final String JVM_CLASS = "jvm-class";

    /**
     * 每10s更新一次pid List
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void updatePidList() {
        try {
            pidList = javaService.getJPSInfo().stream().map(JPS::getPid).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("update pid list error", e);
        }
    }

    @Scheduled(cron = "0/5 * * * * ? ")
    public void getJVMMemory() {
        for (Integer pid : pidList) {
            try {
                String message = CommonUtils.generateMessage(javaService.getJVMMemoryInfo(pid), JVM_MEM);
                kafkaProducer.send(JVM_MEM, message);
            } catch (Exception e) {
                log.error("getJVMMemory error, pid:{}", pid, e);
            }
        }
    }

    @Scheduled(cron = "0/5 * * * * ? ")
    public void getJVMClass() {
        for (Integer pid : pidList) {
            try {
                String message = CommonUtils.generateMessage(javaService.getJVMClassInfo(pid), JVM_CLASS);
                kafkaProducer.send(JVM_CLASS, message);
            } catch (Exception e) {
                log.error("getJVMClass error, pid:{}", pid, e);
            }
        }
    }

    @Scheduled(cron = "0/5 * * * * ? ")
    public void getJVMThread() {
        for (Integer pid : pidList) {
            try {
                String message = CommonUtils.generateMessage(javaService.getJVMThreadInfo(pid), JVM_THREAD);
                kafkaProducer.send(JVM_THREAD, message);
            } catch (Exception e) {
                log.error("getJVMThread error, pid:{}", pid, e);
            }
        }
    }
}
