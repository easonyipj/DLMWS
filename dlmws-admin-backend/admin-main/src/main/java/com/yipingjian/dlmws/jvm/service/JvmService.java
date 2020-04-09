package com.yipingjian.dlmws.jvm.service;

import com.yipingjian.dlmws.jvm.entity.JvmData;

public interface JvmService {
    JvmData getJvmData(String ip, int pid, long[] time);
}
