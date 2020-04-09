package com.yipingjian.dlmws.jvm.service;

import com.yipingjian.dlmws.jvm.entity.JVMMemory;

import java.util.List;

public interface JvmMemoryService {
    List<JVMMemory> getJvmMemoryList(String ip, int pid, long start, long end);
}
