package com.yipingjian.dlmws.storm.service;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.storm.entity.Rules;
import com.yipingjian.dlmws.storm.entity.TomcatLogEntity;
import com.yipingjian.dlmws.storm.entity.WarnMsg;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WarnMessageService {
    public static String generateWarnMsg(String log, String project, String keyword) {
        WarnMsg warnMsg = new WarnMsg();
        warnMsg.setProject(project);
        warnMsg.setKeyword(keyword);
        warnMsg.setLog(log);
        return JSONObject.toJSONString(warnMsg);
    }
}
