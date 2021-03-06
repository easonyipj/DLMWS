package com.yipingjian.dlmws.java.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JVMThread {
    private String hostIp;
    private Integer pid;
    private Integer total;
    private Integer runnable;
    private Integer timeWaiting;
    private Integer waiting;
    private Date time;

}
