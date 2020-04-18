package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yipingjian.dlmws.storm.common.CommonConstant;
import com.yipingjian.dlmws.storm.entity.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.commands.JedisCommands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class WarningBolt extends BaseRichBolt{

    private OutputCollector outputCollector;
    private LinkedHashMap<String, List<Rules>> rulesMap;
    private JedisPool jedisPool;

//    public WarningBolt(JedisPoolConfig config) {
//        super(config);
//    }
//
//    public WarningBolt(JedisClusterConfig config) {
//        super(config);
//    }

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        this.rulesMap = Maps.newLinkedHashMap();
        this.jedisPool = new JedisPool(new redis.clients.jedis.JedisPoolConfig(), "127.0.0.1", 6379);
    }

    @Override
    public void execute(Tuple tuple) {
        String logType;
        JedisCommands jedisCommands = null;
        try {
            String value = tuple.getStringByField("value");
            logType = tuple.getStringByField("type");
            jedisCommands = jedisPool.getResource();
            // tomcat
            if (CommonConstant.TOMCAT.equals(logType)) {
                TomcatLogEntity tomcatLogEntity = JSONObject.parseObject(value, TomcatLogEntity.class);
                // 查看map中是否包含project
                String project = tomcatLogEntity.getProject();
                if(rulesMap.containsKey(project)) {
                    rulesMap.get(project).forEach(rule -> {
                        // 命中关键字
                        if(tomcatLogEntity.getLogMessage().contains(rule.getKeywords())) {
                            if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())) {
                                // 发送到 intervalBolt 进行统计处理
                                log.info("warning-bolt: interval process, match rules:{}", rule.toString());
                            }else{
                                // 组装消息发送到kafka写入Bolt
                                log.info("warning-bolt: immediate process, match rules:{}", rule.toString());
                            }
                        }
                    });
                }else {
                    // 从redis中获取rules
                    String ruleString = jedisCommands.get(project);
                    List<Rules> rules = JSONArray.parseArray(ruleString, Rules.class);
                    rulesMap.put(project, rules);
                    rulesMap.get(project).forEach(rule -> {
                        // 命中关键字
                        if(tomcatLogEntity.getLogMessage().contains(rule.getKeywords())) {
                            if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())) {
                                // 发送到 intervalBolt 进行统计处理
                                log.info("warning-bolt: interval process, match rules:{}", rule.toString());
                            }else{
                                // 组装消息发送到kafka写入Bolt
                                log.info("warning-bolt: immediate process, match rules:{}", rule.toString());
                            }
                        }
                    });
                }
            }
            // log.info("warning data \n{}", entity);

        } catch (Exception e) {
            log.error("warning error, tuple:{}", tuple.toString(), e);
        }

        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}

