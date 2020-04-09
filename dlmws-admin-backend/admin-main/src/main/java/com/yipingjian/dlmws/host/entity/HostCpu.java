package com.yipingjian.dlmws.host.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "host_cpu")
public class HostCpu {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String hostIp;
    private Double userCpu;
    private Double sysCpu;
    private Date time;
}
