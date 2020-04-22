package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.storm.common.CommonConstant;
import com.yipingjian.dlmws.storm.config.JedisPoolConfig;
import com.yipingjian.dlmws.storm.config.LRUMapUtil;
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
    private JedisCommands jedisCommands;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
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
                if(!LRUMapUtil.RULE_MAP.containsKey(project)) {
                    // TODO 加锁
                    String ruleString = jedisCommands.get(project);
                    List<Rule> rules = JSONArray.parseArray(ruleString, Rule.class);
                    if(rules == null) {
                        rules = Lists.newArrayList();
                    }
                    LRUMapUtil.RULE_MAP.put(project, rules);
                }
                // 匹配关键字
                @SuppressWarnings("unchecked")
                List<Rule> rules = (List<Rule>)LRUMapUtil.RULE_MAP.get(project);
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

