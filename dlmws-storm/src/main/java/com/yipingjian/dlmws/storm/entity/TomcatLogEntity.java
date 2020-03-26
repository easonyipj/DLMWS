package com.yipingjian.dlmws.storm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class TomcatLogEntity extends LogEntity {
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
