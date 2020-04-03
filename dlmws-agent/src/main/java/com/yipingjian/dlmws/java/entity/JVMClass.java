package com.yipingjian.dlmws.java.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JVMClass {
    private String hostIp;
    private Integer pid;
    private Integer classCount;
    private Date time;
}
