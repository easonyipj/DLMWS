package com.yipingjian.dlmws.host.task;


import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.common.entity.AgentInfo;
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

    private static final String HOST_CPU = "host_cpu";
    private static final String HOST_MEM = "host_mem";

    @Scheduled(cron = "*/5 * * * * ? ")
    public void getCPUInfo() {
        CPU cpu = hostService.getCPULoad();
        String message = generateMessage(cpu, HOST_CPU);
        kafkaProducer.send(HOST_CPU, message);
    }

    @Scheduled(cron = "*/5 * * * * ? ")
    public void getMemoryInfo() {
        Memory memory = hostService.getMemoryLoad();
        String message = generateMessage(memory, HOST_MEM);
        kafkaProducer.send(HOST_MEM, message);
    }

    private String generateMessage(Object object, String type) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(object));
        JSONObject fields = new JSONObject();
        fields.put("type", type);
        fields.put("project", AgentInfo.STATIC_CLIENT_NAME);
        jsonObject.put("fields", fields);
        return jsonObject.toJSONString();
    }

}
