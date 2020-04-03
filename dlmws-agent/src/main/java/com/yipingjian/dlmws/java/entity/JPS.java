package com.yipingjian.dlmws.java.entity;

import lombok.Data;

@Data
public class JPS {
    private Integer pid;
    private String name;

    public JPS(Integer pid, String name) {
        this.pid = pid;
        this.name = name;
    }
}
