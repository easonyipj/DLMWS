package com.yipingjian.dlmws.tomcat.service;

import com.yipingjian.dlmws.tomcat.entity.*;

import java.util.List;

public interface TomcatLogService {
    List<TomcatLog> getTomcatLogs(EsRequestVo requestVo);
    List<LogCountGroupByProjectPerMin> getLogCountGroupByProjectPerMin(List<String> projects, String from, String to);
    List<LogCountGroupByLevelPerProject> getLogCountGroupByLevelPerProject(List<String> projects, String from, String to);
    List<TopCount> getTopCount(String filed, List<String> projects, String from, String to, Integer size);
}
