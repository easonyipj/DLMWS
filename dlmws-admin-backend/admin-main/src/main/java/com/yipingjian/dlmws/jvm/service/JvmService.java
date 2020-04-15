package com.yipingjian.dlmws.jvm.service;

import com.yipingjian.dlmws.jvm.entity.JvmData;
import com.yipingjian.dlmws.jvm.entity.JvmEntity;

import java.util.List;

public interface JvmService {
    JvmData getJvmData(String ip, int pid, long[] time);
    List<JvmEntity> getJvmList(String owner);
}
