package com.yipingjian.dlmws.storm.entity;

import lombok.Data;

@Data
public class WarnMsg {
    private TomcatLogEntity log;
    private Rule rule;
}