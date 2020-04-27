package com.yipingjian.dlmws.storm.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Rule implements Serializable {
    private Integer id;
    private String project;
    private String logType;
    private String keyword;
    private String type;
    private Double threshold;
    private Integer intervalTime;
    private String dingTalkId;
    private String email;
    private Boolean status;
    private String owner;
}
