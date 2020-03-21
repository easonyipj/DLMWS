package com.yipingjian.dlmws.storm.entity;

import lombok.Data;

@Data
public class Agent {
    private String hostname;
    private String id;
    private String ephemeral_id;
    private String version;
    private String type;
}
