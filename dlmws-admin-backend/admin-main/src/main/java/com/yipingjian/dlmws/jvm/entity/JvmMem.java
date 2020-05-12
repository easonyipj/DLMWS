package com.yipingjian.dlmws.jvm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class JvmMem {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String hostIp;
    private Integer pid;
    private Double memUsed;
    private Double memCapacity;
    private Date time;
}
