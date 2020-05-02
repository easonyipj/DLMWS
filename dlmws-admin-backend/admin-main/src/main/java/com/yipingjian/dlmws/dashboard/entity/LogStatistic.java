package com.yipingjian.dlmws.dashboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class LogStatistic {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer count;
    private Date time;
    private String owner;
    private String type;

}
