package com.yipingjian.dlmws.storm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
public class TomcatLogEntity extends LogEntity implements Serializable {
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
    private Date occurredTime;
    /**
     * agentId
     */
    private String agentId;
    /**
     * host
     */
    private String hostName;
    /**
     * 日志所在进程
     */
    private String pid;
    /**
     * 日志所在线程
     */
    private String threadPosition;
    /**
     * 日志所在类
     */
    private String classPosition;
    /**
     * 日志等级 ERROR INFO DEBUG
     */
    private String level;
    /**
     * 错误堆栈
     */
    private String stacktrace;
    /**
     * 错误类型
     */
    private String errorType;

}
