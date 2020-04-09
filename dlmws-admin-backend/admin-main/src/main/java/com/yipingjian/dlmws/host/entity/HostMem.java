package com.yipingjian.dlmws.host.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "host_mem")
public class HostMem {
    private String hostIp;
    private Double memUsed;
    private Double memUsedRate;
    private Double swapUsed;
    private Double swapUsedRate;
    private Date time;
}
