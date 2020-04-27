package com.yipingjian.dlmws.storm.service;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.storm.entity.Rule;
import com.yipingjian.dlmws.storm.entity.WarnMsg;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class WarnMessageService {
    public static String generateWarnMsg(String ip, long occurredTime, String logText, Rule rule) {
        WarnMsg warnMsg = new WarnMsg();
        warnMsg.setIp(ip);
        warnMsg.setOccurredTime(occurredTime);
        warnMsg.setLogText(logText);
        warnMsg.setRule(rule);
        return JSONObject.toJSONString(warnMsg);
    }
}
