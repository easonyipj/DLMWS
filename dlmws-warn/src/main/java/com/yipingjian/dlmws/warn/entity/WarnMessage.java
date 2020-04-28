package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

@Data
public class WarnMessage {
    private String ip;
    private long occurredTime;
    private String logText;
    private Rule rule;
}
