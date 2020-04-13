package com.yipingjian.dlmws.tomcat.entity;

import lombok.Data;

import java.util.List;

@Data
public class LogCountGroupByLevelPerProject {
    private String project;
    private List<LogCount> logCounts;
    public LogCountGroupByLevelPerProject(String project, List<LogCount> counts) {
        this.project = project;
        this.logCounts = counts;
    }
}
