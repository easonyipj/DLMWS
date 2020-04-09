package com.yipingjian.dlmws.jvm.service;

import com.yipingjian.dlmws.jvm.entity.JVMThread;

import java.util.List;

public interface JvmThreadService {
    List<JVMThread> getJvmThreadList(String ip, int pid, long start, long end);
}
