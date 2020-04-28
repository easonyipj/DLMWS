package com.yipingjian.dlmws.warn.controller;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.utils.PageUtils;
import com.yipingjian.dlmws.utils.Response;
import com.yipingjian.dlmws.warn.entity.WarnRecordStatistic;
import com.yipingjian.dlmws.warn.entity.WarnRecordVo;
import com.yipingjian.dlmws.warn.entity.WarnStatisticVo;
import com.yipingjian.dlmws.warn.service.WarnRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/warn")
public class WarnRecordController {

    @Resource
    private WarnRecordService warnRecordService;

    @GetMapping("/list")
    public Response list(WarnRecordVo warnRecordVo) {
        PageUtils pageUtils = null;
        // TODO 获取登录用户名
        String owner = "yipingjian";
        warnRecordVo.setOwner(owner);
        try {
            pageUtils = warnRecordService.getWarnRecordByPage(warnRecordVo);
        } catch (Exception e) {
            log.error("get warn record list error", e);
        }
        return Response.ok().put("data", pageUtils);
    }

    @GetMapping("/statistic")
    public Response statistic(WarnStatisticVo warnStatisticVo) {
        WarnRecordStatistic warnRecordStatistic = new WarnRecordStatistic();
        // TODO 校验查询项目是否具有权限
        List<String> projects = Lists.newArrayList(warnStatisticVo.getProjects().split(","));
        try {
            // warnRecordStatistic.setWarnCounts(warnRecordService.getWarnCount(warnStatisticVo, projects));
            warnRecordStatistic.setLogTypeCounts(warnRecordService.getLogTypeCount(warnStatisticVo, projects));
        } catch (Exception e) {
            log.error("get warn record statistic error", e);
        }
        return Response.ok().put("data", warnRecordStatistic);
    }

}
