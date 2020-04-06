package com.yipingjian.dlmws.storm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class JVMClass extends Entity{
    private String hostIp;
    private Integer pid;
    private Integer classLoaded;
    private Integer classCompiled;
    private Date time;
}
