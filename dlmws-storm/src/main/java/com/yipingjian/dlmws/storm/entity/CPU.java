package com.yipingjian.dlmws.storm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CPU extends HostLogEntity{
    private String hostIp;
    private Float userCpu;
    private Float systemCpu;
    private Date time;
}
