package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

import java.util.List;

@Data
public class WarnCount {
    private String project;
    private List<WarnCountUnit> warnCountUnits;
}
