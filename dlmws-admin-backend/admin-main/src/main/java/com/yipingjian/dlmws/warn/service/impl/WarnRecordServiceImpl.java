package com.yipingjian.dlmws.warn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yipingjian.dlmws.utils.DateTimeUtil;
import com.yipingjian.dlmws.utils.PageUtils;
import com.yipingjian.dlmws.warn.entity.WarnRecord;
import com.yipingjian.dlmws.warn.entity.WarnRecordVo;
import com.yipingjian.dlmws.warn.mapper.WarnRecordMapper;
import com.yipingjian.dlmws.warn.service.WarnRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class WarnRecordServiceImpl extends ServiceImpl<WarnRecordMapper, WarnRecord> implements WarnRecordService {
    @Override
    public PageUtils getWarnRecordByPage(WarnRecordVo warnRecordVo) {

        IPage<WarnRecord> pageResult;
        // 设置分页参数
        long currentPage = warnRecordVo.getCurrentPage();
        long pageSize = warnRecordVo.getPageSize();
        IPage<WarnRecord> pageParams = new Page<>(currentPage, pageSize);
        // 设置查询参数
        QueryWrapper<WarnRecord> queryWrapper = generateWarnRecordQuery(warnRecordVo);
        pageResult = this.page(pageParams, queryWrapper);

        return new PageUtils(pageResult);
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
