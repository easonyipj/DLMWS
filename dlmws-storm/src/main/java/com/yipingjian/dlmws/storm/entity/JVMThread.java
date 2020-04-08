package com.yipingjian.dlmws.storm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class JVMThread extends Entity {
    private String hostIp;
    private Integer pid;
    private Integer total;
    private Integer runnable;
    private Integer timeWaiting;
    private Integer waiting;
    private Date time;

}
