package com.yipingjian.dlmws.tomcat.entity;

import lombok.Data;

import java.util.List;

@Data
public class TopCount {
    private String project;
    private List<LogCount> logCounts;
    public TopCount(String project, List<LogCount> counts) {
        this.project = project;
        this.logCounts = counts;
    }
}
