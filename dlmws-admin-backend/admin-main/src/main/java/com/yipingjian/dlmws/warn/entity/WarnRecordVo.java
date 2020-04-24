package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

@Data
public class WarnRecordVo {
    private int pageSize;
    private int currentPage;
    private String project;
    private String ip;
    private String keyword;
    private String type;
    private Integer dingTalkStatus;
    private Integer emailStatus;
    private String occurredTime;
    private String warningTime;
    private String owner;
}
