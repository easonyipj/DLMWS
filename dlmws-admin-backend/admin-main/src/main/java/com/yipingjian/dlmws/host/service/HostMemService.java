package com.yipingjian.dlmws.host.service;

import com.yipingjian.dlmws.host.entity.HostMem;

import java.util.List;

public interface HostMemService {
    List<HostMem> getHostMemList(String ip, long start, long end);
}
