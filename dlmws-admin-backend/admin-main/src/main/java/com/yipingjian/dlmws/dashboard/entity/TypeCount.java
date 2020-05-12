package com.yipingjian.dlmws.dashboard.entity;

import lombok.Data;

@Data
public class TypeCount {
    private String type;
    private Integer count;
    public TypeCount(String type) {
        this.type = type;
        this.count = 0;
    }
}
