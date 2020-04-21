package com.yipingjian.dlmws.storm.entity;

import lombok.Data;

@Data
public class Rule {
    private Integer id;
    private String project;
    private String keyword;
    private String type;
    private Integer threshold;
    private Integer interval;
    private String dingTalkId;
    private String email;
    private Boolean status;
    private String owner;
}
