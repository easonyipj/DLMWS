package com.yipingjian.dlmws.storm.service;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.storm.entity.Agent;
import com.yipingjian.dlmws.storm.entity.Host;
import com.yipingjian.dlmws.storm.entity.TomcatLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: 后期优化时考虑配置化解决
 */
@Slf4j
public class LogFormatService {

    public static TomcatLogEntity formatTomcatLog(JSONObject jsonObject) {
        TomcatLogEntity tomcatLogEntity = JSONObject.parseObject(jsonObject.toString(), TomcatLogEntity.class);
        Agent agent = JSONObject.parseObject(jsonObject.getString("agent"), Agent.class);
        tomcatLogEntity.setAgentId(agent.getId());
        tomcatLogEntity.setHostName(agent.getHostname());
        // 如果获取不到线程名称 则将message字段放进logMessage
        if(StringUtils.isEmpty(tomcatLogEntity.getThreadPosition())) {
            tomcatLogEntity.setLogMessage(jsonObject.getString("message"));
            return tomcatLogEntity;
        }
        // 将部分字段前后的空格去掉
        tomcatLogEntity.setThreadPosition(tomcatLogEntity.getThreadPosition().trim());
        tomcatLogEntity.setClassPosition(tomcatLogEntity.getClassPosition().trim());
        tomcatLogEntity.setLevel(tomcatLogEntity.getLevel().trim());
        // 格式化Java错误堆栈
        if (StringUtils.isNotEmpty(tomcatLogEntity.getStacktrace())) {
            // 过滤java errorStack的"\n"
            String stackTrace = tomcatLogEntity.getStacktrace().replaceFirst("\n", "");
            tomcatLogEntity.setStacktrace(stackTrace);
            // 过滤:
            String errorType = stackTrace.split("\n")[0].split(":")[0];
            tomcatLogEntity.setErrorType(errorType);
        }
        if(tomcatLogEntity.getLevel().equals("ERROR") && StringUtils.isEmpty(tomcatLogEntity.getStacktrace())) {
            tomcatLogEntity.setErrorType(tomcatLogEntity.getLogMessage());
        }
        return tomcatLogEntity;
    }

}
