package com.yipingjian.dlmws.storm.entity;

import lombok.Data;

@Data
public class Host {
    private String hostname;
    private String name;
    private OS os;
    private String id;
    private String architecture;
}
