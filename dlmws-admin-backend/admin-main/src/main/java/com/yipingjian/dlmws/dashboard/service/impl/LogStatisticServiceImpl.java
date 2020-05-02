package com.yipingjian.dlmws.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.dashboard.entity.LogStatistic;
import com.yipingjian.dlmws.dashboard.entity.LogStatisticSummary;
import com.yipingjian.dlmws.dashboard.mapper.LogStatisticMapper;
import com.yipingjian.dlmws.dashboard.service.LogStatisticService;
import com.yipingjian.dlmws.warn.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class LogStatisticServiceImpl extends ServiceImpl<LogStatisticMapper, LogStatistic> implements LogStatisticService {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public LogStatisticSummary getLogStatisticSummary(String owner, String from, String to) {
        updateLogCount(owner);
        updateWarnCount(owner);
        List<LogStatistic> logCountList = getLogCount(owner, from, to);
        List<LogStatistic> warnCountList = getWarnCount(owner, from, to);
        LogStatisticSummary logStatisticSummary = new LogStatisticSummary();
        logStatisticSummary.setLogCount(logCountList.get(logCountList.size() - 1).getCount());
        logStatisticSummary.setWarnCount(warnCountList.get(warnCountList.size() - 1).getCount());
        logStatisticSummary.setLogCountList(logCountList);
        logStatisticSummary.setWarnCountList(warnCountList);
        return logStatisticSummary;
    }

    @Override
    public List<LogStatistic> getLogCount(String owner, String from, String to) {
        return list(new QueryWrapper<LogStatistic>().eq("owner", owner).eq("type", "log-count").ge("time", from).le("time", to));
    }

    @Override
    public List<LogStatistic> getWarnCount(String owner, String from, String to) {
        return list(new QueryWrapper<LogStatistic>().eq("owner", owner).eq("type", "log-count").ge("time", from).le("time", to));
    }

    @Override
    public void updateLogCount(String owner) {
        List<String> projects = Lists.newArrayList("dlmws-agent", "dlmws-log");
        Integer count = 0;
        LogStatistic latest = this.getOne(new QueryWrapper<LogStatistic>().eq("onwer", owner).eq("type", "log-count").orderByDesc("time"));
        if(latest != null) {
            count = latest.getCount();
        }

        for (String project : projects) {
            count += redisUtil.getCount(project + ":count:INFO");
            count += redisUtil.getCount(project + ":count:ERROR");
            count += redisUtil.getCount(project + ":count:DEBUG");
            count += redisUtil.getCount(project + ":count:WARN");
        }
        LogStatistic logStatistic = new LogStatistic();
        logStatistic.setCount(count);
        logStatistic.setOwner(owner);
        logStatistic.setTime(new Date(System.currentTimeMillis()));
        logStatistic.setType("log-count");
        this.save(logStatistic);
    }

    @Override
    public void updateWarnCount(String owner) {
        List<String> projects = Lists.newArrayList("dlmws-agent", "dlmws-log");
        Integer count = 0;
        LogStatistic latest = this.getOne(new QueryWrapper<LogStatistic>().eq("onwer", owner).eq("type", "warn-count").orderByDesc("time"));
        if(latest != null) {
            count = latest.getCount();
        }

        for (String project : projects) {
            count += redisUtil.getCount(project + ":warn:tomcat");
            count += redisUtil.getCount(project + ":warn:host-mem");
            count += redisUtil.getCount(project + ":warn:host-cpu");
            count += redisUtil.getCount(project + ":warn:jvm-mem");
            count += redisUtil.getCount(project + ":warn:jvm-thread");
            count += redisUtil.getCount(project + ":warn:jvm-class");
        }
        LogStatistic logStatistic = new LogStatistic();
        logStatistic.setCount(count);
        logStatistic.setOwner(owner);
        logStatistic.setTime(new Date(System.currentTimeMillis()));
        logStatistic.setType("warn-count");
        this.save(logStatistic);
    }
}
