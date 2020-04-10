package com.yipingjian.dlmws.jvm.service;

import com.yipingjian.dlmws.jvm.entity.JvmThread;

import java.util.List;

public interface JvmThreadService {
    List<JvmThread> getJvmThreadList(String ip, int pid, long start, long end);
}
