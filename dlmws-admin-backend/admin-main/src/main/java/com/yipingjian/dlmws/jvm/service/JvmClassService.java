package com.yipingjian.dlmws.jvm.service;

import com.yipingjian.dlmws.jvm.entity.JVMClass;

import java.util.List;

public interface JvmClassService {
    List<JVMClass> getJvmClassList(String ip, int pid, long start, long end);
}
