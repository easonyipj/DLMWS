package com.yipingjian.dlmws.jvm.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JvmThread {
    private String hostIp;
    private Integer pid;
    private Integer total;
    private Integer runnable;
    private Integer timeWaiting;
    private Integer waiting;
    private Date time;

}
