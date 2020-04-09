package com.yipingjian.dlmws.jvm.service.impl;

import com.yipingjian.dlmws.jvm.entity.JvmData;
import com.yipingjian.dlmws.jvm.service.JvmClassService;
import com.yipingjian.dlmws.jvm.service.JvmMemoryService;
import com.yipingjian.dlmws.jvm.service.JvmService;
import com.yipingjian.dlmws.jvm.service.JvmThreadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service
public class JvmServiceImpl implements JvmService {

    @Resource
    private JvmThreadService jvmThreadService;
    @Resource
    private JvmClassService jvmClassService;
    @Resource
    private JvmMemoryService jvmMemoryService;

    private static final Long THREE_HUNDREDS_MIN = 24L * 60 * 60 * 1000;

    @Override
    public JvmData getJvmData(String ip, int pid, long[] time) {
        JvmData jvmData = new JvmData();
        long st = time[0];
        long ed = time[1];
        try {
            if(st == 0 || ed == 0) {
                // default is 300min
                st = System.currentTimeMillis() - THREE_HUNDREDS_MIN;
                ed = System.currentTimeMillis();
            }
            // TODO  后期可以考虑用线程池优化
            jvmData.setClassList(jvmClassService.getJvmClassList(ip, pid, st, ed));
            jvmData.setMemoryList(jvmMemoryService.getJvmMemoryList(ip, pid, st, ed));
            jvmData.setThreadList(jvmThreadService.getJvmThreadList(ip, pid, st, ed));
        } catch (Exception e) {
            log.error("get jvm data error", e);
        }

        return jvmData;
    }
}
