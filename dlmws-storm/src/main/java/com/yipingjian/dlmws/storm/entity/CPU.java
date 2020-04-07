package com.yipingjian.dlmws.storm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CPU extends HostLogEntity{
    private String hostIp;
    private Double userCpu;
    private Double systemCpu;
    private Date time;
}
