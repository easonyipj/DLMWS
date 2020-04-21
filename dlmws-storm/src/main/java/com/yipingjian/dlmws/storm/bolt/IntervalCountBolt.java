package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.storm.config.JedisPoolConfig;

import com.yipingjian.dlmws.storm.entity.Rule;
import com.yipingjian.dlmws.storm.entity.TomcatLogEntity;
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
public class IntervalCountBolt extends BaseRichBolt {

    private OutputCollector outputCollector;
    private JedisCommands jedisCommands;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        this.jedisCommands = JedisPoolConfig.jedisPool.getResource();
    }

    @Override
    public void execute(Tuple tuple) {
        TomcatLogEntity tomcatLogEntity = (TomcatLogEntity) tuple.getValueByField("log");
        Rule rule = (Rule) tuple.getValueByField("rule");

        String project = tomcatLogEntity.getProject();
        long occurredTime = tomcatLogEntity.getOccurredTime().getTime();

        String key = project + "&" + rule.getKeyword();

        List<Long> timeSeq;
        String queue = jedisCommands.get(key);

        if(queue == null) {
            timeSeq = Lists.newArrayList();
        }else {
            timeSeq = JSONArray.parseArray(queue, Long.class);
        }

        // 插入当前时间到队尾
        timeSeq.add(occurredTime);
        // 已满则从对头开始删除与当前日志产生时间大于interval的数据
        if(timeSeq.size() > rule.getThreshold()) {
            Long date = occurredTime - rule.getInterval() * 1000;
            while (timeSeq.size() > 0 && timeSeq.get(0) < date) {
                timeSeq.remove(0);
            }
        }
        // 删除后如果队列长度依然超过则报警
        if(timeSeq.size() > rule.getThreshold()) {
            String message = WarnMessageService.generateWarnMsg(tomcatLogEntity, rule);
            log.info("interval-bolt: send warning msg {}", message);
            outputCollector.emit(new Values(message));
        }
        // 更新并设置过期时间
        jedisCommands.setex(key, rule.getInterval(), JSONArray.toJSONString(timeSeq));

        log.info("interval-bolt: process log{}", tomcatLogEntity);
        // log.info("interval-bolt: 性能测试");
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message"));
    }
}
