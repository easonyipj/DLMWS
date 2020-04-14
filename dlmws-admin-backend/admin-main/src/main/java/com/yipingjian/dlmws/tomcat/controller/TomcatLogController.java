package com.yipingjian.dlmws.tomcat.controller;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.tomcat.entity.*;
import com.yipingjian.dlmws.tomcat.service.TomcatLogService;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/tomcat")
public class TomcatLogController {

    @Resource
    private TomcatLogService tomcatLogService;

    @GetMapping("/search")
    public Response search(EsRequestVo esRequestVo) {
        // TODO : 校验用户请求合法性 不允许查未授权的项目
        List<TomcatLog> tomcatLogList = tomcatLogService.getTomcatLogs(esRequestVo);
        return Response.ok().put("data", tomcatLogList);
    }

    @GetMapping("/logCount/project/min")
    public Response getLogCountGroupByProjectPerMin(LogCountVo logCountVo) {
        // TODO : 校验用户请求合法性 不允许查未授权的项目
        List<String> projects = Lists.newArrayList(logCountVo.getProjects().split(","));
        List<LogCountGroupByProjectPerMin> list = tomcatLogService.getLogCountGroupByProjectPerMin(projects, logCountVo.getFrom(), logCountVo.getTo());
        return Response.ok().put("data", list);
    }

    @GetMapping("/logCount/level/project")
    public Response getLogCountGroupByLevelPerProject(LogCountVo logCountVo) {
        // TODO : 校验用户请求合法性 不允许查未授权的项目
        List<String> projects = Lists.newArrayList(logCountVo.getProjects().split(","));
        List<LogCountGroupByLevelPerProject> list = tomcatLogService.getLogCountGroupByLevelPerProject(projects, logCountVo.getFrom(), logCountVo.getTo());
        return Response.ok().put("data", list);
    }

    @GetMapping("/logCount/top")
    public Response getTopThreadCount(LogCountVo logCountVo) {
        // TODO : 校验用户请求合法性 不允许查未授权的项目
        List<String> projects = Lists.newArrayList(logCountVo.getProjects().split(","));
        List<TopCount> list = tomcatLogService.getTopCount(logCountVo.getType(), projects, logCountVo.getFrom(), logCountVo.getTo(), 10);
        return Response.ok().put("data", list);
    }
}
