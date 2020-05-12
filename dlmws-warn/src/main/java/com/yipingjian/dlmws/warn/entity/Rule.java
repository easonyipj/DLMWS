package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

@Data
public class Rule{
    private Integer id;
    private String project;
    private String keyword;
    private String logType;
    private String type;
    private Double threshold;
    private Integer intervalTime;
    private String dingTalkId;
    private String email;
    private Boolean status;
    private String owner;
}
