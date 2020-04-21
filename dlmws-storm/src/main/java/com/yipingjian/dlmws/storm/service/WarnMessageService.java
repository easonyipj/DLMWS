package com.yipingjian.dlmws.storm.service;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.storm.entity.Rule;
import com.yipingjian.dlmws.storm.entity.TomcatLogEntity;
import com.yipingjian.dlmws.storm.entity.WarnMsg;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WarnMessageService {
    public static String generateWarnMsg(TomcatLogEntity log, Rule rule) {
        WarnMsg warnMsg = new WarnMsg();
        warnMsg.setLog(log);
        warnMsg.setRule(rule);
        return JSONObject.toJSONString(warnMsg);
    }
}
