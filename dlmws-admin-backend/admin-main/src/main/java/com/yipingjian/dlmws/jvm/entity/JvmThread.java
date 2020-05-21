package com.yipingjian.dlmws.jvm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class JvmThread implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String hostIp;
    private Integer pid;
    private Integer total;
    private Integer runnable;
    private Integer timeWaiting;
    private Integer waiting;
    private Date time;

}
