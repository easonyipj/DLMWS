package com.yipingjian.dlmws.java.service;

import com.yipingjian.dlmws.java.entity.JPS;
import com.yipingjian.dlmws.java.entity.JVMClass;
import com.yipingjian.dlmws.java.entity.JVMMemory;
import com.yipingjian.dlmws.java.entity.JVMThread;

import java.util.List;

public interface JavaService {
    List<JPS> getJPSInfo() throws Exception;

    JVMClass getJVMClassInfo(Integer pid) throws Exception;

    JVMMemory getJVMMemoryInfo(Integer pid) throws Exception;

    JVMThread getJVMThreadInfo(Integer pid) throws Exception;
}
