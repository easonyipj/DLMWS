package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONObject;

import com.yipingjian.dlmws.storm.common.CommonConstant;
import com.yipingjian.dlmws.storm.common.LRUMapUtil;
import com.yipingjian.dlmws.storm.common.RuleUtil;
import com.yipingjian.dlmws.storm.entity.*;
import com.yipingjian.dlmws.storm.service.WarnMessageService;
import lombok.extern.slf4j.Slf4j;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.List;
import java.util.Map;

@Slf4j
public class WarningBolt extends BaseRichBolt{

    private OutputCollector outputCollector;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(Tuple tuple) {
        String logType;
        String project;
        boolean warn = false;
        try {
            String value = tuple.getStringByField("value");
            logType = tuple.getStringByField("type");
            project = tuple.getStringByField("project");

            // 判断项目和日志类型是否有报警规则
            if(LRUMapUtil.PROJECT_MAP.containsKey(project)) {
                List<String> types = (List<String>)LRUMapUtil.PROJECT_MAP.get(project);
                if(types.contains(logType)) {
                    warn = true;
                }
            }

            // tomcat
            if (warn && CommonConstant.TOMCAT.equals(logType)) {
                TomcatLogEntity tomcatLogEntity = JSONObject.parseObject(value, TomcatLogEntity.class);
                List<Rule> rules = LRUMapUtil.getRules(project, CommonConstant.TOMCAT);
                // 匹配关键字
                rules.forEach(rule -> {
                    // 检查规则
                    RuleResult ruleResult = RuleUtil.checkTomcatRule(rule, tomcatLogEntity, value);
                    if(CommonConstant.INTERVAL_TYPE.equals(ruleResult.getWarnType())) {
                        outputCollector.emit(CommonConstant.INTERVAL_TYPE, new Values(tomcatLogEntity, rule.getKeyword(), rule));
                    }
                    if(CommonConstant.IMMEDIATE_TYPE.equals(ruleResult.getWarnType())) {
                        outputCollector.emit(CommonConstant.IMMEDIATE_TYPE, new Values(ruleResult.getMessage()));
                        String key = project + ":" + "warn:" + logType;
                        outputCollector.emit(CommonConstant.COUNT, new Values(key));
                    }
                });
            }

            // host mem
            if(warn && CommonConstant.HOST_MEM.equals(logType)) {
                List<Rule> rules = LRUMapUtil.getRules(project, CommonConstant.HOST_MEM);
                rules.forEach(rule -> {
                    RuleResult ruleResult = RuleUtil.checkHostMemRule(rule, value);
                    if(CommonConstant.INTERVAL_TYPE.equals(ruleResult.getWarnType())) {
                        outputCollector.emit(CommonConstant.EWMA_TYPE, new Values(ruleResult.getRate(), ruleResult.getIp(), value, ruleResult.getTime(), rule));
                    }
                    if(CommonConstant.IMMEDIATE_TYPE.equals(ruleResult.getWarnType())) {
                        outputCollector.emit(CommonConstant.IMMEDIATE_TYPE, new Values(ruleResult.getMessage()));
                        String key = project + ":" + "warn:" + logType;
                        outputCollector.emit(CommonConstant.COUNT, new Values(key));
                    }
                });
            }

            // host cpu
            if(warn && CommonConstant.HOST_CPU.equals(logType)) {
                List<Rule> rules = LRUMapUtil.getRules(project, CommonConstant.HOST_CPU);
                rules.forEach(rule -> {
                    RuleResult ruleResult = RuleUtil.checkHostCpuRule(rule, value);
                    if(CommonConstant.INTERVAL_TYPE.equals(ruleResult.getWarnType())) {
                        outputCollector.emit(CommonConstant.EWMA_TYPE, new Values(ruleResult.getRate(), ruleResult.getIp(), value, ruleResult.getTime(), rule));
                    }
                    if(CommonConstant.IMMEDIATE_TYPE.equals(ruleResult.getWarnType())) {
                        outputCollector.emit(CommonConstant.IMMEDIATE_TYPE, new Values(ruleResult.getMessage()));
                        String key = project + ":" + "warn:" + logType;
                        outputCollector.emit(CommonConstant.COUNT, new Values(key));
                    }
                });
            }

            // jvm mem
            if(warn && CommonConstant.JVM_MEM.equals(logType)) {
                List<Rule> rules = LRUMapUtil.getRules(project, CommonConstant.JVM_MEM);
                rules.forEach(rule -> {
                    RuleResult ruleResult = RuleUtil.checkJvmMemRule(rule, value);
                    if(CommonConstant.INTERVAL_TYPE.equals(ruleResult.getWarnType())) {
                        outputCollector.emit(CommonConstant.EWMA_TYPE, new Values(ruleResult.getRate(), ruleResult.getIp(), value, ruleResult.getTime(), rule));
                    }
                    if(CommonConstant.IMMEDIATE_TYPE.equals(ruleResult.getWarnType())) {
                        outputCollector.emit(CommonConstant.IMMEDIATE_TYPE, new Values(ruleResult.getMessage()));
                        String key = project + ":" + "warn:" + logType;
                        outputCollector.emit(CommonConstant.COUNT, new Values(key));
                    }
                });
            }

        } catch (Exception e) {
            log.error("warning error, tuple:{}", tuple.toString(), e);
        }
        // log.info("warning-bolt: process");
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(CommonConstant.INTERVAL_TYPE, new Fields("log", "keyword", "rule"));
        outputFieldsDeclarer.declareStream(CommonConstant.EWMA_TYPE, new Fields("value", "ip", "log", "time", "rule"));
        outputFieldsDeclarer.declareStream(CommonConstant.IMMEDIATE_TYPE, new Fields("message"));
        outputFieldsDeclarer.declareStream(CommonConstant.COUNT, new Fields("key"));
    }
}

