package com.yipingjian.dlmws.tomcat.entity;

import lombok.Data;

import java.util.List;

@Data
public class LogCountGroupByProjectPerMin {
    private String project;
    private List<LogCountMills> logCounts;
    public LogCountGroupByProjectPerMin(String project, List<LogCountMills> counts){
        this.project = project;
        this.logCounts = counts;
    }
}
