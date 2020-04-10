package com.yipingjian.dlmws.jvm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yipingjian.dlmws.jvm.entity.JvmClass;
import com.yipingjian.dlmws.jvm.mapper.JvmClassMapper;
import com.yipingjian.dlmws.jvm.service.JvmClassService;
import com.yipingjian.dlmws.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class JvmClassServiceImpl extends ServiceImpl<JvmClassMapper, JvmClass> implements JvmClassService {
    @Override
    public List<JvmClass> getJvmClassList(String ip, int pid, long start, long end) {
        String st = DateTimeUtil.getTimestampFormat(start);
        String ed = DateTimeUtil.getTimestampFormat(end);
        QueryWrapper<JvmClass> queryWrapper = new QueryWrapper<JvmClass>().eq("host_ip", ip).eq("pid", pid)
                .ge("time", st).le("time", ed);
        return list(queryWrapper);
    }
}
