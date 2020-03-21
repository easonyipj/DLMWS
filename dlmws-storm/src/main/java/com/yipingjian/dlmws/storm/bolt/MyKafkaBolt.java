package com.yipingjian.dlmws.storm.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class MyKafkaBolt extends BaseRichBolt {

    private OutputCollector outputCollector;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        System.out.println("topic:" + tuple.getValueByField("topic"));
        System.out.println("partition:" + tuple.getValueByField("partition"));
        System.out.println("offset:" + tuple.getValueByField("offset"));
        System.out.println("key:" + tuple.getValueByField("key"));
        System.out.println("value:" + tuple.getValueByField("value"));
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        // outputFieldsDeclarer.declare(new Fields("message"));
    }
}
