package com.yipingjian.dlmws.storm.entity;

import lombok.Data;

@Data
public class Rules {
    private String keywords;
    private String type;
    private Integer threshold;
    private Integer interval;
}
