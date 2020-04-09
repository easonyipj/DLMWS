package com.yipingjian.dlmws.jvm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yipingjian.dlmws.jvm.entity.JVMMemory;
import com.yipingjian.dlmws.jvm.mapper.JvmMemoryMapper;
import com.yipingjian.dlmws.jvm.service.JvmMemoryService;
import com.yipingjian.dlmws.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class JvmMemoryServiceImpl extends ServiceImpl<JvmMemoryMapper, JVMMemory> implements JvmMemoryService {
    @Override
    public List<JVMMemory> getJvmMemoryList(String ip, int pid, long start, long end) {
        String st = DateTimeUtil.getTimestampFormat(start);
        String ed = DateTimeUtil.getTimestampFormat(end);
        QueryWrapper<JVMMemory> queryWrapper = new QueryWrapper<JVMMemory>().eq("host_ip", ip).eq("pid", pid)
                .ge("time", st).le("time", ed);
        return list(queryWrapper);
    }
}
