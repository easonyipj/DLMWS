package com.yipingjian.dlmws.tomcat.controller;

import com.yipingjian.dlmws.tomcat.entity.EsRequestVo;
import com.yipingjian.dlmws.tomcat.entity.TomcatLog;
import com.yipingjian.dlmws.tomcat.service.ElasticSearchService;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/es")
public class ElasticSearchController {

    @Resource
    private ElasticSearchService elasticSearchService;

    @GetMapping("/search")
    public Response search(@RequestBody EsRequestVo esRequestVo) {
        List<TomcatLog> tomcatLogList = elasticSearchService.getTomcatLogs(esRequestVo);
        return Response.ok().put("data", tomcatLogList);
    }
}
