package com.yipingjian.dlmws.tomcat.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LogCountPerMin {
    private Long key;
    private int docCount;
}
