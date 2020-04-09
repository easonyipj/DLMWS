package com.yipingjian.dlmws.host.service;

import com.yipingjian.dlmws.host.entity.HostCpu;

import java.util.List;

public interface HostCpuService {
    List<HostCpu> getHostCpuList(String ip, long start, long end);
}
