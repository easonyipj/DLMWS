package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

import java.util.List;

@Data
public class LogTypeCount {
    private String project;
    private List<LogTypeCountUnit> logTypeCountUnits;
}
