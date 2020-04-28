package com.yipingjian.dlmws.kafka;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.warn.entity.WarnMessage;
import com.yipingjian.dlmws.warn.service.WarnRecordService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class KafkaConsumer {

    @Resource
    private WarnRecordService warnService;

    @KafkaListener(topics = "warning")
    public void onMessage(String message){
        WarnMessage warnMessage = JSONObject.parseObject(message, WarnMessage.class);
        //System.out.println("receive warning message");
        warnService.processWarnMessage(warnMessage);
    }
}
