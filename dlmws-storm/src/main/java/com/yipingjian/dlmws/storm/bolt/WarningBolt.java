package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.storm.entity.LogEntity;
import com.yipingjian.dlmws.storm.entity.TomcatLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

@Slf4j
public class WarningBolt extends BaseRichBolt {

    private static final String TOMCAT = "tomcat";
    private OutputCollector outputCollector;
    private LogEntity logEntity;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        this.logEntity = new LogEntity();
    }

    @Override
    public void execute(Tuple tuple) {
        String type = null;
        try {
            String value = tuple.getStringByField("value");
            type = tuple.getStringByField("type");
            if(TOMCAT.equals(type)) {
                logEntity = JSONObject.parseObject(value, TomcatLogEntity.class);
            }
            log.info("warning data \n{}", logEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        outputCollector.emit(Lists.newArrayList(JSONObject.toJSONString(logEntity), type));
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("value", "type"));
    }
}

