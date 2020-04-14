package com.yipingjian.dlmws.host.service;

import com.yipingjian.dlmws.host.entity.Host;
import com.yipingjian.dlmws.host.entity.HostDataEntity;

import java.util.List;

public interface HostService {
    HostDataEntity getHostData(String ip, long[] cpuTime);
    List<Host> getHostList(String owner);
}
