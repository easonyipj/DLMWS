package com.yipingjian.dlmws.jvm.entity;

import lombok.Data;

import java.util.List;

@Data
public class JvmData {
    private List<JvmClass> classList;
    private List<JvmMem> memoryList;
    private List<JvmThread> threadList;
}
