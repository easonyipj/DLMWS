package com.yipingjian.dlmws.host.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yipingjian.dlmws.host.entity.Host;
import com.yipingjian.dlmws.host.entity.HostDataEntity;
import com.yipingjian.dlmws.host.mapper.HostMapper;
import com.yipingjian.dlmws.host.service.HostCpuService;
import com.yipingjian.dlmws.host.service.HostMemService;
import com.yipingjian.dlmws.host.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class HostServiceImpl extends ServiceImpl<HostMapper, Host> implements HostService {

    @Resource
    private HostCpuService hostCpuService;
    @Resource
    private HostMemService hostMemService;

    private static final Long THIRTY_MIN = 30L * 60 * 1000;

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

    @Override
    public List<Host> getHostList(String owner) {
        return list(new QueryWrapper<Host>().eq("owner", owner));
    }

    @Override
    public void addHost(Host host) {
        this.saveOrUpdate(host);
    }
}
