package com.yipingjian.dlmws.warn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.utils.DateTimeUtil;
import com.yipingjian.dlmws.utils.PageUtils;
import com.yipingjian.dlmws.warn.entity.*;
import com.yipingjian.dlmws.warn.mapper.WarnRecordMapper;
import com.yipingjian.dlmws.warn.service.WarnRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class WarnRecordServiceImpl extends ServiceImpl<WarnRecordMapper, WarnRecord> implements WarnRecordService {

    @Resource
    private WarnRecordMapper warnRecordMapper;

    @Override
    public PageUtils getWarnRecordByPage(WarnRecordVo warnRecordVo) {

        IPage<WarnRecord> pageResult;
        // 设置分页参数
        long currentPage = warnRecordVo.getCurrentPage();
        long pageSize = warnRecordVo.getPageSize();
        IPage<WarnRecord> pageParams = new Page<>(currentPage, pageSize);
        // 设置查询参数
        QueryWrapper<WarnRecord> queryWrapper = generateWarnRecordQuery(warnRecordVo);
        queryWrapper.orderByDesc("warning_time");
        pageResult = this.page(pageParams, queryWrapper);

        return new PageUtils(pageResult);
    }

    @Override
    public List<WarnCount> getWarnCount(WarnStatisticVo warnStatisticVo, List<String> projects) {
        List<WarnCount> list = Lists.newArrayList();

        projects.forEach(project -> {
            WarnCount warnCount = new WarnCount();
            warnCount.setProject(project);
            List<WarnCountUnit> warnCountUnits = warnRecordMapper.getWarnCount(project, warnStatisticVo.getFrom(), warnStatisticVo.getTo());
            warnCount.setWarnCountUnits(warnCountUnits);
            list.add(warnCount);
        });

        return list;
    }

    @Override
    public List<LogTypeCount> getLogTypeCount(WarnStatisticVo warnStatisticVo, List<String> projects) {
        List<LogTypeCount> list = Lists.newArrayList();

        projects.forEach(project -> {
            LogTypeCount logTypeCount = new LogTypeCount();
            logTypeCount.setProject(project);
            List<LogTypeCountUnit> logTypeCountUnits = warnRecordMapper.getLogTypeCount(project, warnStatisticVo.getFrom(), warnStatisticVo.getTo());
            logTypeCount.setLogTypeCountUnits(logTypeCountUnits);
            list.add(logTypeCount);
        });

        return list;
    }

    private QueryWrapper<WarnRecord> generateWarnRecordQuery(WarnRecordVo warnRecordVo) {
        QueryWrapper<WarnRecord> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(warnRecordVo.getOwner())) {
            queryWrapper.eq("owner", warnRecordVo.getOwner());
        }
        if(!StringUtils.isEmpty(warnRecordVo.getProject())) {
            queryWrapper.eq("project", warnRecordVo.getProject());
        }
        if(!StringUtils.isEmpty(warnRecordVo.getIp())) {
            queryWrapper.eq("ip", warnRecordVo.getIp());
        }
        if(!StringUtils.isEmpty(warnRecordVo.getKeyword())) {
            queryWrapper.eq("keyword", warnRecordVo.getKeyword());
        }
        if(!StringUtils.isEmpty(warnRecordVo.getType())) {
            queryWrapper.eq("type", warnRecordVo.getType());
        }
        if(!StringUtils.isEmpty(warnRecordVo.getDingTalkStatus())) {
            queryWrapper.eq("ding_talk_status", warnRecordVo.getDingTalkStatus());
        }
        if(!StringUtils.isEmpty(warnRecordVo.getEmailStatus())) {
            queryWrapper.eq("email_status", warnRecordVo.getEmailStatus());
        }
        if(!StringUtils.isEmpty(warnRecordVo.getOccurredTime())) {
            String[] times = warnRecordVo.getOccurredTime().split(",");
            String st = DateTimeUtil.getTimestampFormat(Long.parseLong(times[0]));
            String ed = DateTimeUtil.getTimestampFormat(Long.parseLong(times[1]));
            queryWrapper.ge("occurred_time", st).le("occurred_time", ed);
        }
        if(!StringUtils.isEmpty(warnRecordVo.getWarningTime())) {
            String[] times = warnRecordVo.getWarningTime().split(",");
            String st = DateTimeUtil.getTimestampFormat(Long.parseLong(times[0]));
            String ed = DateTimeUtil.getTimestampFormat(Long.parseLong(times[1]));
            queryWrapper.ge("occurred_time", st).le("occurred_time", ed);
        }

        return queryWrapper;
    }
}
