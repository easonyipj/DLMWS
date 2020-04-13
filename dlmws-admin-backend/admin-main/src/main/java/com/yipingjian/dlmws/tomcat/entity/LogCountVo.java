package com.yipingjian.dlmws.tomcat.entity;

import lombok.Data;

import java.util.List;

@Data
public class LogCountVo {
    private String type;
    private Integer size;
    private List<String> projects;
    private String from;
    private String to;
}
