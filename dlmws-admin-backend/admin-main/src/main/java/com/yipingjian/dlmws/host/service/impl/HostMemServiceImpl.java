package com.yipingjian.dlmws.host.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yipingjian.dlmws.host.entity.HostMem;
import com.yipingjian.dlmws.host.mapper.HostMemMapper;
import com.yipingjian.dlmws.host.service.HostMemService;
import com.yipingjian.dlmws.utils.DateTimeUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HostMemServiceImpl extends ServiceImpl<HostMemMapper, HostMem> implements HostMemService {

    @Override
    public List<HostMem> getHostMemList(String ip, long start, long end) {
        String st = DateTimeUtil.getTimestampFormat(start);
        String ed = DateTimeUtil.getTimestampFormat(end);
        QueryWrapper<HostMem> queryWrapper = new QueryWrapper<HostMem>().eq("host_ip", ip)
                .ge("time", st).le("time", ed);
        return list(queryWrapper);
    }
}
