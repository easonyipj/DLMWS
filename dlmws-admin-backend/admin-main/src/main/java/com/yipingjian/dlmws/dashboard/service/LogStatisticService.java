package com.yipingjian.dlmws.dashboard.service;

import com.yipingjian.dlmws.dashboard.entity.LogStatistic;
import com.yipingjian.dlmws.dashboard.entity.LogStatisticSummary;

import java.util.List;

public interface LogStatisticService {
    List<LogStatistic> getLogCount(String owner, String from, String to);
    List<LogStatistic> getWarnCount(String owner, String from, String to);
    LogStatisticSummary getLogStatisticSummary(String owner, String from, String to);
    void updateLogCount(String owner);
    void updateWarnCount(String owner);
}
