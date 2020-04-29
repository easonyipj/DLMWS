package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

import java.util.List;

@Data
public class WarnRecordStatistic {
    private List<WarnCount> warnCounts;
    private List<TypeCount> logTypeCounts;
    private List<TypeCountUnit> projectCounts;
    private List<TypeCount> keyWordCounts;
    private List<TypeCount> ruleTypeCounts;
    private List<StatusCount> dingWarnStatusCounts;
    private List<StatusCount> emailWarnStatusCounts;
}
