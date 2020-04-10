package com.yipingjian.dlmws.jvm.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JvmMem {
    private String hostIp;
    private Integer pid;
    private Double memUsed;
    private Double memCapacity;
    private Date time;
}
