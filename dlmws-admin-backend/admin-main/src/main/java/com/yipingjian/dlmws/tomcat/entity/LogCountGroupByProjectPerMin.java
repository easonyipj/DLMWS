package com.yipingjian.dlmws.tomcat.entity;

import lombok.Data;

import java.util.List;

@Data
public class LogCountGroupByProjectPerMin {
    private String project;
    private List<LogCountPerMin> counts;
    public LogCountGroupByProjectPerMin(String project, List<LogCountPerMin> counts){
        this.project = project;
        this.counts = counts;
    }
}
