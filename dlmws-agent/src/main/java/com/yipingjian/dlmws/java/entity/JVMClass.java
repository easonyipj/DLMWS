package com.yipingjian.dlmws.java.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JVMClass {
    private String hostIp;
    private Integer pid;
    private Integer classLoaded;
    private Integer classCompiled;
    private Date time;
}
