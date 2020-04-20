package com.yipingjian.dlmws.storm.entity;

import lombok.Data;

@Data
public class WarnMsg {
    private String project;
    private String keyword;
    private String log;
}
