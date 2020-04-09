package com.yipingjian.dlmws.jvm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yipingjian.dlmws.jvm.entity.JVMThread;
import com.yipingjian.dlmws.jvm.mapper.JvmThreadMapper;
import com.yipingjian.dlmws.jvm.service.JvmThreadService;
import com.yipingjian.dlmws.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class JvmThreadServiceImpl extends ServiceImpl<JvmThreadMapper, JVMThread> implements JvmThreadService {
    @Override
    public List<JVMThread> getJvmThreadList(String ip, int pid, long start, long end) {
        String st = DateTimeUtil.getTimestampFormat(start);
        String ed = DateTimeUtil.getTimestampFormat(end);
        QueryWrapper<JVMThread> queryWrapper = new QueryWrapper<JVMThread>().eq("host_ip", ip).eq("pid", pid)
                .ge("time", st).le("time", ed);
        return list(queryWrapper);
    }
}
