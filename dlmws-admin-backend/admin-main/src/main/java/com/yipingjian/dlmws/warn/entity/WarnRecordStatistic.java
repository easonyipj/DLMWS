package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

import java.util.List;

@Data
public class WarnRecordStatistic {
    private List<WarnCount> warnCounts;
    private List<LogTypeCount> logTypeCounts;
}
