package com.yipingjian.dlmws.storm.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LogEntity {
    /**
     * 日志所在项目 filebeat中配置
     */
    private String project;
    /**
     * 日志类型 filebeat中配置
     */
    private String logType;
    /**
     * 日志类容
     */
    private String logMessage;
    /**
     * 发生时间
     */
    private String occurredTime;
    /**
     * agentId
     */
    private String agentId;
    /**
     * host
     */
    private String hostName;
}
