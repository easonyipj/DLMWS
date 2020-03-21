package com.yipingjian.dlmws.storm.entity;

import lombok.Data;

@Data
public class OS {
    private String platform;
    private String name;
    private String version;
    private String family;
    private String kernel;
    private String build;
}
