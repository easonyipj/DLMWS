package com.yipingjian.dlmws.host.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yipingjian.dlmws.host.entity.HostCpu;
import com.yipingjian.dlmws.host.mapper.HostCpuMapper;
import com.yipingjian.dlmws.host.service.HostCpuService;
import com.yipingjian.dlmws.utils.DateTimeUtil;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class HostCpuServiceImpl extends ServiceImpl<HostCpuMapper, HostCpu> implements HostCpuService {

    @Override
    public List<HostCpu> getHostCpuList(String ip, long start, long end) {
        String st = DateTimeUtil.getTimestampFormat(start);
        String ed = DateTimeUtil.getTimestampFormat(end);
        QueryWrapper<HostCpu> queryWrapper = new QueryWrapper<HostCpu>().eq("host_ip", ip)
                .ge("time", st).le("time", ed);
        return list(queryWrapper);
    }
}
