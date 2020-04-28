package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

import java.util.Date;

@Data
public class WarnCountUnit {
    private Date time;
    private int count;
}
