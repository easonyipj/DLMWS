package com.yipingjian.dlmws.storm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class JVMMemory extends Entity{
    private String hostIp;
    private Integer pid;
    private Float memoryUsed;
    private Float memoryCapacity;
    private Date time;
}
