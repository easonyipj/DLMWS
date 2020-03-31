package com.yipingjian.dlmws.host.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CPU {
    private String hostIp;
    private Float userCpu;
    private Float systemCpu;
    private Date time;
}
