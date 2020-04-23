package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

@Data
public class WarnMessage {
    private TomcatLogEntity log;
    private Rule rule;
}
