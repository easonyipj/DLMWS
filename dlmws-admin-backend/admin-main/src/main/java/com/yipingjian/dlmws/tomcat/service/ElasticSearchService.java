package com.yipingjian.dlmws.tomcat.service;

import com.yipingjian.dlmws.tomcat.entity.EsRequestVo;
import com.yipingjian.dlmws.tomcat.entity.TomcatLog;

import java.util.List;

public interface ElasticSearchService {
    List<TomcatLog> getTomcatLogs(EsRequestVo requestVo);
}
