package com.yipingjian.dlmws.java.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JVMThread {
    private String hostIp;
    private Integer pid;
    private Integer threadCount;
    private Integer daemonThreadCount;
    private Date time;
}
