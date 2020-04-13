package com.yipingjian.dlmws.tomcat.controller;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.tomcat.entity.*;
import com.yipingjian.dlmws.tomcat.service.TomcatLogService;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.List;

@Slf4j
@RestController
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

    @PostMapping("/logCount/project/min")
    public Response getLogCountGroupByProjectPerMin(@RequestBody LogCountVo logCountVo) {
        // TODO : 校验用户请求合法性 不允许查未授权的项目 时间格式化成ISO标准格式
        List<LogCountGroupByProjectPerMin> list = tomcatLogService.getLogCountGroupByProjectPerMin(logCountVo.getProjects(), logCountVo.getFrom(), logCountVo.getTo());
        return Response.ok().put("data", list);
    }

    @GetMapping("/logCount/level/project")
    public Response getLogCountGroupByLevelPerProject() {
        // TODO : 校验用户请求合法性 不允许查未授权的项目 时间格式化成ISO标准格式
        List<String> projects = Lists.newArrayList("dlmws-log", "dlmws-agent");
        String from = "2020-04-10T22:37:00.000";
        String to = "2020-04-12T22:38:00.000";
        List<LogCountGroupByLevelPerProject> list = tomcatLogService.getLogCountGroupByLevelPerProject(projects, from, to);
        return Response.ok().put("data", list);
    }

    @GetMapping("/logCount/top/thread")
    public Response getTopThreadCount() {
        // TODO : 校验用户请求合法性 不允许查未授权的项目 时间格式化成ISO标准格式
        List<String> projects = Lists.newArrayList("dlmws-log", "dlmws-agent");
        String from = "2020-04-10T22:37:00.000";
        String to = "2020-04-12T22:38:00.000";
        List<TopCount> list = tomcatLogService.getTopCount("errorType.keyword", projects, from, to, 10);
        return Response.ok().put("data", list);
    }
}
