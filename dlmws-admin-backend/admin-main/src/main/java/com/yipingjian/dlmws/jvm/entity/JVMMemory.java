package com.yipingjian.dlmws.jvm.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JVMMemory {
    private String hostIp;
    private Integer pid;
    private Double memoryUsed;
    private Double memoryCapacity;
    private Date time;
}
