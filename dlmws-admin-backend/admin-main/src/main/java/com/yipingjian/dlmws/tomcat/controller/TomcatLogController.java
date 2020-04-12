package com.yipingjian.dlmws.tomcat.controller;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.tomcat.entity.EsRequestVo;
import com.yipingjian.dlmws.tomcat.entity.TomcatLog;
import com.yipingjian.dlmws.tomcat.service.TomcatLogService;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @GetMapping("/logCount/project/min")
    public Response getLogCountGroupByProjectPerMin() {
        // TODO : 校验用户请求合法性 不允许查未授权的项目
        tomcatLogService.getLogCountGroupByProjectPerMin(Lists.newArrayList("dlmws-log", "dlmws-agent"),null, null);
        return Response.ok();
    }
}
