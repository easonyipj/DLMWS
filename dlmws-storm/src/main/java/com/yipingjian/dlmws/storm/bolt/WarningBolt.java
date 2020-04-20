package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.yipingjian.dlmws.storm.common.CommonConstant;
import com.yipingjian.dlmws.storm.config.JedisPoolConfig;
import com.yipingjian.dlmws.storm.entity.*;
import com.yipingjian.dlmws.storm.service.WarnMessageService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.map.LRUMap;
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
    private LRUMap rulesMap;
    private JedisCommands jedisCommands;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        this.rulesMap = new LRUMap();
        this.jedisCommands = JedisPoolConfig.jedisPool.getResource();
    }

    @Override
    public void execute(Tuple tuple) {
        String logType;

        try {
            String value = tuple.getStringByField("value");
            logType = tuple.getStringByField("type");
            // tomcat
            if (CommonConstant.TOMCAT.equals(logType)) {
                TomcatLogEntity tomcatLogEntity = JSONObject.parseObject(value, TomcatLogEntity.class);
                // 查看map中是否包含project
                String project = tomcatLogEntity.getProject();
                if(!rulesMap.containsKey(project)) {
                    String ruleString = jedisCommands.get(project);
                    List<Rules> rules = JSONArray.parseArray(ruleString, Rules.class);
                    rulesMap.put(project, rules);
                }
                // 匹配关键字
                @SuppressWarnings("unchecked")
                List<Rules> rules = (List<Rules>)rulesMap.get(project);
                rules.forEach(rule -> {
                    // 命中关键字
                    if(tomcatLogEntity.getLogMessage().contains(rule.getKeywords())) {
                        if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())) {
                            // 发送到 intervalBolt 进行统计处理
                            log.info("warning-bolt: interval process, match rules:{}", rule.toString());
                            outputCollector.emit(CommonConstant.INTERVAL_TYPE, new Values(value, rule.getKeywords(), rule.getThreshold(), rule.getInterval()));
                        }else{
                            // 组装消息发送到kafka写入Bolt
                            String message = WarnMessageService.generateWarnMsg(value, project, rule.getKeywords());
                            log.info("warning-bolt: immediate process, match rules:{}, message:{}", rule.toString(), message);
                            outputCollector.emit(CommonConstant.IMMEDIATE_TYPE, new Values(value));
                        }
                    }
                });
            }

        } catch (Exception e) {
            log.error("warning error, tuple:{}", tuple.toString(), e);
        }

        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(CommonConstant.INTERVAL_TYPE, new Fields("value", "keyword", "threshold", "interval"));
        outputFieldsDeclarer.declareStream(CommonConstant.IMMEDIATE_TYPE, new Fields("message"));
    }
}

