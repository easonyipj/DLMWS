package com.yipingjian.dlmws.host.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Memory {
    private String hostIp;
    private Float memoryUsed;
    private Float memoryUsedRate;
    private Float swapUsed;
    private Float swapUsedRate;
    private Date time;
}
