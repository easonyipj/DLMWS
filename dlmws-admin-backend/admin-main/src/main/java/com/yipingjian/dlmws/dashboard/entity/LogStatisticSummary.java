package com.yipingjian.dlmws.dashboard.entity;

import lombok.Data;

import java.util.List;

@Data
public class LogStatisticSummary {
    private Integer logCount;
    private Integer warnCount;
    private List<LogStatistic> logCountList;
    private List<LogStatistic> warnCountList;
}
