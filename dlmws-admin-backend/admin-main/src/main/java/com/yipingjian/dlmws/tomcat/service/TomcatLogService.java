package com.yipingjian.dlmws.tomcat.service;

import com.yipingjian.dlmws.tomcat.entity.EsRequestVo;
import com.yipingjian.dlmws.tomcat.entity.LogCountGroupByProjectPerMin;
import com.yipingjian.dlmws.tomcat.entity.TomcatLog;

import java.util.Date;
import java.util.List;

public interface TomcatLogService {
    List<TomcatLog> getTomcatLogs(EsRequestVo requestVo);
    List<LogCountGroupByProjectPerMin> getLogCountGroupByProjectPerMin(List<String> projects, Date from, Date to);
}
