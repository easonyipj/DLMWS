package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.storm.common.CommonConstant;
import com.yipingjian.dlmws.storm.entity.*;
import com.yipingjian.dlmws.storm.service.LogFormatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

@Slf4j
public class LogFormatBolt extends BaseRichBolt {


    private OutputCollector outputCollector;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        JSONObject jsonObject;
        JSONObject fields;
        Entity entity = null;
        String logType;
        String project;
        String ip;
        try {
            jsonObject = JSONObject.parseObject((String) tuple.getValueByField("value"));
            fields = (JSONObject) jsonObject.get("fields");
            logType = fields.getString("type");
            project = fields.getString("project");
            ip = fields.getString("ip");

            // tomcat 日志
            if (CommonConstant.TOMCAT.equals(logType)) {
                TomcatLogEntity tomcatLogEntity = LogFormatService.formatTomcatLog(jsonObject);
                tomcatLogEntity.setProject(project);
                tomcatLogEntity.setLogType(logType);
                tomcatLogEntity.setIp(ip);
                entity = tomcatLogEntity;
            }

            // host日志
            if (CommonConstant.HOST_MEM.equals(logType)) {
                entity = JSONObject.parseObject(jsonObject.toJSONString(), Memory.class);
            }
            if (CommonConstant.HOST_CPU.equals(logType)) {
                entity = JSONObject.parseObject(jsonObject.toJSONString(), CPU.class);
            }

            // jvm 日志
            if (CommonConstant.JVM_CLASS.equals(logType)) {
                entity = JSONObject.parseObject(jsonObject.toJSONString(), JVMClass.class);
            }
            if (CommonConstant.JVM_THREAD.equals(logType)) {
                entity = JSONObject.parseObject(jsonObject.toJSONString(), JVMThread.class);
            }
            if (CommonConstant.JVM_MEM.equals(logType)) {
                entity = JSONObject.parseObject(jsonObject.toJSONString(), JVMMemory.class);
            }

            outputCollector.emit(Lists.newArrayList(JSONObject.toJSONString(entity), logType, project));

        } catch (Exception e) {
            log.error("格式化日志异常, 日志内容:\n {}", tuple.getStringByField("value"), e);
        }

        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("value", "type", "project"));
    }
}
