package com.yipingjian.dlmws.host.task;


import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.common.entity.AgentInfo;
import com.yipingjian.dlmws.common.utils.CommonUtils;
import com.yipingjian.dlmws.host.entity.CPU;
import com.yipingjian.dlmws.host.entity.Memory;
import com.yipingjian.dlmws.host.service.HostService;
import com.yipingjian.dlmws.kafka.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class HostMonitorTask {

    @Resource
    private KafkaProducer kafkaProducer;
    @Resource
    private HostService hostService;

    private static final String HOST_CPU = "host-cpu";
    private static final String HOST_MEM = "host-mem";

    @Scheduled(cron = "3/5 * * * * ? ")
    public void getCPUInfo() {
        CPU cpu = hostService.getCPULoad();
        String message = CommonUtils.generateMessage(cpu, HOST_CPU);
        kafkaProducer.send(HOST_CPU, message);
    }

    @Scheduled(cron = "3/5 * * * * ? ")
    public void getMemoryInfo() {
        Memory memory = hostService.getMemoryLoad();
        String message = CommonUtils.generateMessage(memory, HOST_MEM);
        kafkaProducer.send(HOST_MEM, message);
    }




}
