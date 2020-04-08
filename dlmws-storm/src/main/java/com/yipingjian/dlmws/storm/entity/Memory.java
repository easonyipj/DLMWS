package com.yipingjian.dlmws.storm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class Memory extends HostLogEntity {
    private String hostIp;
    private Double memoryUsed;
    private Double memoryUsedRate;
    private Double swapUsed;
    private Double swapUsedRate;
    private Date time;
}
