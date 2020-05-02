package com.yipingjian.dlmws.storm.bolt;

import lombok.Data;

@Data
public class RuleResult {
    private String warnType;
    private String message;
    private String value;
    private Double rate;
    private String ip;
    private Long time;
}
