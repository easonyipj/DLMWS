package com.yipingjian.dlmws.storm.bolt;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yipingjian.dlmws.storm.common.EWMA;
import com.yipingjian.dlmws.storm.entity.Rule;
import com.yipingjian.dlmws.storm.service.WarnMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;


import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public class EWMABolt extends BaseRichBolt {

    private OutputCollector outputCollector;
    private Cache<String, EWMA> ewmaCache;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        this.ewmaCache = CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES).build();
    }

    @Override
    public void execute(Tuple tuple) {
        Rule rule = (Rule) tuple.getValueByField("rule");
        long time = tuple.getLongByField("time");
        String key = rule.getProject() + ":" + rule.getKeyword() + ":" + rule.getThreshold() + ":" + rule.getIntervalTime();
        double value = tuple.getDoubleByField("value");
        EWMA ewma = ewmaCache.getIfPresent(key);
        if(ewma == null) {
           ewma = new EWMA().sliding(rule.getIntervalTime() * 1000).withAlphaInterval(rule.getIntervalTime());
           ewmaCache.put(key, ewma);
        }

        if(value >= rule.getThreshold()) {
            ewma.mark(time, value);
        }

        System.out.println(key + "-" + ewma.getAverage());
        if(ewma.getAverageIn(EWMA.Time.SECONDS) <= rule.getIntervalTime()) {
            // 发送消息到报警队列
            String logText = tuple.getStringByField("log");
            String ip = tuple.getStringByField("ip");
            String message = WarnMessageService.generateWarnMsg(ip, time, logText, rule);
            // log.info("send warning message, {}", message);
            // outputCollector.emit(new Values(message));
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message"));
    }
}
