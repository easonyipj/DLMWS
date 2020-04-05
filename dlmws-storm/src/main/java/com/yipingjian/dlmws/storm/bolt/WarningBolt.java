package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
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

import java.util.Map;

@Slf4j
public class WarningBolt extends BaseRichBolt {

    private OutputCollector outputCollector;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String logType;
        String stream = null;
        Object entity = null;
        try {
            String value = tuple.getStringByField("value");
            logType = tuple.getStringByField("type");

            // tomcat日志
            if (CommonConstant.TOMCAT.equals(logType)) {
                entity = JSONObject.parseObject(value, TomcatLogEntity.class);
                stream = CommonConstant.TOMCAT;
            }
            // host memory日志
            if(CommonConstant.HOST_MEM.equals(logType)) {
                entity = JSONObject.parseObject(value, Memory.class);
                stream = CommonConstant.HOST_CPU;
            }
            // host cpu日志
            if(CommonConstant.HOST_CPU.equals(logType)) {
                entity = JSONObject.parseObject(value, CPU.class);
                stream = CommonConstant.HOST_CPU;
            }

            outputCollector.emit(stream, new Values(JSONObject.toJSONString(entity), logType));
            log.info("warning data \n{}", entity);

        } catch (Exception e) {
            log.error("warning error, tuple:{}", tuple.toString(), e);
        }

        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(CommonConstant.TOMCAT, new Fields("value", "type"));
        outputFieldsDeclarer.declareStream(CommonConstant.HOST_CPU, new Fields("value", "type"));
        outputFieldsDeclarer.declareStream(CommonConstant.HOST_MEM, new Fields("value", "type"));
    }
}

