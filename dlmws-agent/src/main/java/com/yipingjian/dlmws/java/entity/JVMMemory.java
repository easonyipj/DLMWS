package com.yipingjian.dlmws.java.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JVMMemory {
    private String hostIp;
    private Integer pid;
    private Float memoryUsed;
    private Float memoryCapacity;
    private Date time;
}
