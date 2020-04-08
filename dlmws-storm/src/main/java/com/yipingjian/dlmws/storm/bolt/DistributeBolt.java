package com.yipingjian.dlmws.storm.bolt;

import com.alibaba.fastjson.JSONObject;
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
public class DistributeBolt extends BaseRichBolt {
    private OutputCollector outputCollector;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {

        try {
            String value = tuple.getStringByField("value");
            String logType = tuple.getStringByField("type");

            // tomcat日志
            if (CommonConstant.TOMCAT.equals(logType)) {
                TomcatLogEntity tomcatLogEntity = JSONObject.parseObject(value, TomcatLogEntity.class);
                outputCollector.emit(CommonConstant.TOMCAT, new Values(JSONObject.toJSONString(tomcatLogEntity), logType));
            }

            // host 日志
            if (CommonConstant.HOST_MEM.equals(logType)) {
                Memory memory = JSONObject.parseObject(value, Memory.class);
                outputCollector.emit(CommonConstant.HOST_MEM, new Values(memory.getHostIp(), memory.getMemoryUsed(), memory.getMemoryUsedRate(), memory.getSwapUsed(),
                        memory.getSwapUsedRate(), memory.getTime().getTime()));
            }
            if (CommonConstant.HOST_CPU.equals(logType)) {
                CPU cpu = JSONObject.parseObject(value, CPU.class);
                outputCollector.emit(CommonConstant.HOST_CPU, new Values(cpu.getHostIp(), cpu.getUserCpu(), cpu.getSystemCpu(), cpu.getTime().getTime()));
            }

            // jvm 日志
            if (CommonConstant.JVM_CLASS.equals(logType)) {
                JVMClass jvmClass = JSONObject.parseObject(value, JVMClass.class);
                outputCollector.emit(CommonConstant.JVM_CLASS, new Values(jvmClass.getHostIp(), jvmClass.getPid(), jvmClass.getClassLoaded(),
                        jvmClass.getClassCompiled(), jvmClass.getTime().getTime()));
            }
            if (CommonConstant.JVM_THREAD.equals(logType)) {
                JVMThread jvmThread = JSONObject.parseObject(value, JVMThread.class);
                outputCollector.emit(CommonConstant.JVM_THREAD, new Values(jvmThread.getHostIp(), jvmThread.getPid(), jvmThread.getTotal(),
                        jvmThread.getRunnable(), jvmThread.getTimeWaiting(), jvmThread.getWaiting(), jvmThread.getTime().getTime()));
            }
            if (CommonConstant.JVM_MEM.equals(logType)) {
                JVMMemory jvmMemory = JSONObject.parseObject(value, JVMMemory.class);
                outputCollector.emit(CommonConstant.JVM_MEM, new Values(jvmMemory.getHostIp(), jvmMemory.getPid(), jvmMemory.getMemoryUsed(),
                        jvmMemory.getMemoryCapacity(), jvmMemory.getTime().getTime()));
            }

        } catch (Exception e) {
            log.error("warning error, tuple:{}", tuple.toString(), e);
        }

        outputCollector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(CommonConstant.TOMCAT, new Fields("value", "type"));
        outputFieldsDeclarer.declareStream(CommonConstant.HOST_CPU, new Fields("host_ip", "user_cpu", "sys_cpu", "time"));
        outputFieldsDeclarer.declareStream(CommonConstant.HOST_MEM, new Fields("host_ip", "mem_used", "mem_used_rate", "swap_used", "swap_used_rate", "time"));
        outputFieldsDeclarer.declareStream(CommonConstant.JVM_CLASS, new Fields("host_ip", "pid", "class_loaded", "class_compiled", "time"));
        outputFieldsDeclarer.declareStream(CommonConstant.JVM_MEM, new Fields("host_ip", "pid", "mem_used", "mem_capacity", "time"));
        outputFieldsDeclarer.declareStream(CommonConstant.JVM_THREAD, new Fields("host_ip", "pid", "total", "runnable", "time_waiting", "waiting", "time"));
    }
}
