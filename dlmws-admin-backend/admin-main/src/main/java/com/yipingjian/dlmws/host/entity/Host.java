package com.yipingjian.dlmws.host.entity;

import lombok.Data;

@Data
public class Host {
    private Long id;
    private String ip;
    private String name;
    private String projects;
    private String owner;
    private String info;
    private Short status;
}
