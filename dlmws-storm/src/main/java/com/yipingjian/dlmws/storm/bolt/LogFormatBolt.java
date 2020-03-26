package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.storm.entity.LogEntity;
import com.yipingjian.dlmws.storm.service.LogFormatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

@Slf4j
public class LogFormatBolt extends BaseRichBolt {

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
        JSONObject jsonObject;
        JSONObject fields;
        String logType = "";
        String project;
        try {
            jsonObject = JSONObject.parseObject((String) tuple.getValueByField("value"));
            fields = (JSONObject) jsonObject.get("fields");
            logType = fields.getString("type");
            project = fields.getString("project");

            // tomcat 日志
            if (TOMCAT.equals(logType)) {
                logEntity = LogFormatService.formatTomcatLog(jsonObject);
                logEntity.setProject(project);
                logEntity.setLogType(logType);
            }
            // mysql 日志

        } catch (Exception e) {
            log.error("格式化日志异常, 日志内容:\n {}", tuple.getStringByField("value"), e);
        }

        outputCollector.emit(Lists.newArrayList(JSONObject.toJSONString(logEntity), logType));
        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("value", "type"));
    }
}
