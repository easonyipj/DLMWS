package com.yipingjian.dlmws.jvm.service;

import com.yipingjian.dlmws.jvm.entity.JvmMem;

import java.util.List;

public interface JvmMemoryService {
    List<JvmMem> getJvmMemoryList(String ip, int pid, long start, long end);
}
