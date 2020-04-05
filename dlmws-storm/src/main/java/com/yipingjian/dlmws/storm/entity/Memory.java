package com.yipingjian.dlmws.storm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class Memory extends HostLogEntity{
    private String hostIp;
    private Float memoryUsed;
    private Float memoryUsedRate;
    private Float swapUsed;
    private Float swapUsedRate;
    private Date time;
}
