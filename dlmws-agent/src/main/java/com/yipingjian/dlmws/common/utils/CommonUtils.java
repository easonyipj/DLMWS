package com.yipingjian.dlmws.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.common.entity.AgentInfo;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;


@Slf4j
public class CommonUtils {

    public static String getHostIp() {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("GET HOST IP ERROR", e);
            return AgentInfo.STATIC_CLIENT_ID;
        }
        return ip;
    }

    public static String generateMessage(Object object, String type) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(object));
        JSONObject fields = new JSONObject();
        fields.put("type", type);
        fields.put("project", AgentInfo.STATIC_CLIENT_NAME);
        jsonObject.put("fields", fields);
        return jsonObject.toJSONString();
    }
}
