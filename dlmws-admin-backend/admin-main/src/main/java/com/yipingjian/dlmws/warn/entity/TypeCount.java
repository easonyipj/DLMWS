package com.yipingjian.dlmws.warn.entity;

import lombok.Data;

import java.util.List;

@Data
public class TypeCount {
    private String project;
    private List<TypeCountUnit> list;
}
