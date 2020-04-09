package com.yipingjian.dlmws.jvm.entity;

import lombok.Data;

import java.util.List;

@Data
public class JvmData {
    private List<JVMClass> classList;
    private List<JVMMemory> memoryList;
    private List<JVMThread> threadList;
}
