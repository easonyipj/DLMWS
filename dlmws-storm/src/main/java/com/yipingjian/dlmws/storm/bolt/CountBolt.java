package com.yipingjian.dlmws.storm.bolt;

import com.yipingjian.dlmws.storm.config.JedisPoolConfig;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import redis.clients.jedis.commands.JedisCommands;

import java.util.Map;

public class CountBolt extends BaseRichBolt {

    private OutputCollector outputCollector;
    private JedisCommands jedisCommands;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        this.jedisCommands = JedisPoolConfig.jedisPool.getResource();
    }

    @Override
    public void execute(Tuple tuple) {
        String key = tuple.getStringByField("key");
        jedisCommands.incr(key);
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
