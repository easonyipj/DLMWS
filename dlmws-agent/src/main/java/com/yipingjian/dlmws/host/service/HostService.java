package com.yipingjian.dlmws.host.service;

import com.yipingjian.dlmws.host.entity.CPU;
import com.yipingjian.dlmws.host.entity.HostBasicInfo;
import com.yipingjian.dlmws.host.entity.Memory;

public interface HostService {
    HostBasicInfo getHostBasicInfo();
    Memory getMemoryLoad();
    CPU getCPULoad();
}
