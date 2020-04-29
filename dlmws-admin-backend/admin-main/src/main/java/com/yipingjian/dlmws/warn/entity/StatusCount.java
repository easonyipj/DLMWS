package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

import java.util.List;

@Data
public class StatusCount {
    private String project;
    private List<StatusCountUnit> list;
}
