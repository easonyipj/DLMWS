package com.yipingjian.dlmws.host.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Host implements Serializable {
    private Long id;
    private String ip;
    private String name;
    private String projects;
    private String owner;
    private String info;
    private Short status;
}
