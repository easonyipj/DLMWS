package com.yipingjian.dlmws.common.utils;

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
}
