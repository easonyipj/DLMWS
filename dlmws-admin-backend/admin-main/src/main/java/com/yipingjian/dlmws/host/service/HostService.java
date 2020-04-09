package com.yipingjian.dlmws.host.service;

import com.yipingjian.dlmws.host.entity.HostDataEntity;

public interface HostService {
    HostDataEntity getHostData(String ip, long[] cpuTime);
}
