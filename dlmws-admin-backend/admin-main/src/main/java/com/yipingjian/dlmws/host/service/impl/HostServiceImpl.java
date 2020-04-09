package com.yipingjian.dlmws.host.service.impl;

import com.yipingjian.dlmws.host.entity.HostDataEntity;
import com.yipingjian.dlmws.host.service.HostCpuService;
import com.yipingjian.dlmws.host.service.HostMemService;
import com.yipingjian.dlmws.host.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class HostServiceImpl implements HostService {

    @Resource
    private HostCpuService hostCpuService;
    @Resource
    private HostMemService hostMemService;

    private static final Long THIRTY_MIN = 24L * 60 * 60 * 1000;

    @Override
    public HostDataEntity getHostData(String ip, long[] cpuInterval) {
        HostDataEntity hostDataEntity = new HostDataEntity();
        long cpuSt = cpuInterval[0];
        long cpuEd = cpuInterval[1];
        try {
            // default is latest 30min
            if(cpuSt == 0 || cpuEd == 0) {
                cpuSt = System.currentTimeMillis() - THIRTY_MIN;
                cpuEd = System.currentTimeMillis();
            }
            // TODO  后期可以考虑用线程池优化
            hostDataEntity.setCpuList(hostCpuService.getHostCpuList(ip, cpuSt, cpuEd));
            hostDataEntity.setMemList(hostMemService.getHostMemList(ip, cpuSt, cpuEd));
        } catch (Exception e) {
            log.error("get jvm data", e);
        }
        return hostDataEntity;
    }
}
