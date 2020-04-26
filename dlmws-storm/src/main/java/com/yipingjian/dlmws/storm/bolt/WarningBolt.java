package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.storm.common.CommonConstant;
import com.yipingjian.dlmws.storm.config.JedisPoolConfig;
import com.yipingjian.dlmws.storm.common.LRUMapUtil;
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
import redis.clients.jedis.commands.JedisCommands;

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
                    // 命中关键字
                    if(tomcatLogEntity.getLogMessage().contains(rule.getKeyword())) {
                        if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())) {
                            // 发送到 intervalBolt 进行统计处理
                            log.info("warning-bolt: interval process, match rules:{}", rule.toString());
                            outputCollector.emit(CommonConstant.INTERVAL_TYPE, new Values(tomcatLogEntity, rule.getKeyword(), rule));
                        }else{
                            // 组装消息发送到kafka写入Bolt
                            String message = WarnMessageService.generateWarnMsg(tomcatLogEntity, rule);
                            //log.info("warning-bolt: 性能测试");
                            log.info("warning-bolt: immediate process, match rules:{}, message:{}", rule.toString(), message);
                            outputCollector.emit(CommonConstant.IMMEDIATE_TYPE, new Values(message));
                        }
                    }
                });
            }

            if(warn && CommonConstant.HOST_MEM.equals(logType)) {
                Memory memory = JSONObject.parseObject(value, Memory.class);
            }

        } catch (Exception e) {
            log.error("warning error, tuple:{}", tuple.toString(), e);
        }

        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(CommonConstant.INTERVAL_TYPE, new Fields("log", "keyword", "rule"));
        outputFieldsDeclarer.declareStream(CommonConstant.IMMEDIATE_TYPE, new Fields("message"));
    }
}

