package com.yipingjian.dlmws.jvm.service;

import com.yipingjian.dlmws.jvm.entity.JvmClass;

import java.util.List;

public interface JvmClassService {
    List<JvmClass> getJvmClassList(String ip, int pid, long start, long end);
}
