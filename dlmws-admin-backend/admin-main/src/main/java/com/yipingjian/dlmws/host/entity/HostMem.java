package com.yipingjian.dlmws.host.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "host_mem")
public class HostMem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String hostIp;
    private Double memUsed;
    private Double memUsedRate;
    private Double swapUsed;
    private Double swapUsedRate;
    private Date time;
}
