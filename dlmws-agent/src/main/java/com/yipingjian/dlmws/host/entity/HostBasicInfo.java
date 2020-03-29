package com.yipingjian.dlmws.host.entity;

import lombok.Data;

@Data
public class HostBasicInfo {
    private String OSType;
    private String system;
    private String firmware;
    private String baseboard;
    private String processors;
    private String memory;
    private String powerSources;
    private String filesystem;
    private String network;
}
