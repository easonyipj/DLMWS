package com.yipingjian.dlmws.host.service;

import com.yipingjian.dlmws.host.entity.Memory;

public interface HostService {
    String getHostBasicInfo();
    Memory getMemoryLoad();
}
