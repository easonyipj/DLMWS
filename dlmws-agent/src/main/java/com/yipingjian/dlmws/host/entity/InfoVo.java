package com.yipingjian.dlmws.host.entity;

import lombok.Data;

import java.util.List;

@Data
public class InfoVo {
    private String type;
    private Integer size;
    private List<String> projects;
    private String from;
    private String to;
}
