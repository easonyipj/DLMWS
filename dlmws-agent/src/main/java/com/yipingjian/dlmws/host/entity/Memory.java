package com.yipingjian.dlmws.host.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Memory {
    private String hostIp;
    private Float memoryUsed;
    private Double memoryUsedRate;
    private Float swapUsed;
    private Double swapUsedRate;
    private Date date;
}
