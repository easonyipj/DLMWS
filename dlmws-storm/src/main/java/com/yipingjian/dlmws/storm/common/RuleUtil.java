package com.yipingjian.dlmws.storm.common;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.storm.bolt.RuleResult;
import com.yipingjian.dlmws.storm.entity.*;
import com.yipingjian.dlmws.storm.service.WarnMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.storm.tuple.Values;

@Slf4j
public class RuleUtil {
    public static RuleResult checkTomcatRule(Rule rule, TomcatLogEntity tomcatLogEntity, String value) {
        RuleResult ruleResult = new RuleResult();
        if(tomcatLogEntity.getLogMessage().contains(rule.getKeyword())) {
            if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())) {
                // 发送到 intervalBolt 进行统计处理
                log.info("warning-bolt: interval process, match rules:{}", rule.toString());
                ruleResult.setWarnType(CommonConstant.INTERVAL_TYPE);
            }else{
                // 组装消息发送到kafka写入Bolt
                String message = WarnMessageService.generateWarnMsg(tomcatLogEntity.getIp(), tomcatLogEntity.getOccurredTime().getTime(), value, rule);
                //log.info("warning-bolt: 性能测试");
                log.info("warning-bolt: immediate process, match rules:{}, message:{}", rule.toString(), message);
                ruleResult.setWarnType(CommonConstant.IMMEDIATE_TYPE);
                ruleResult.setMessage(message);
            }
        }
        return ruleResult;
    }

    public static RuleResult checkHostMemRule(Rule rule, String value) {
        RuleResult ruleResult = new RuleResult();
        Memory memory = JSONObject.parseObject(value, Memory.class);
        if(CommonConstant.MEMORY.equals(rule.getKeyword())) {
            if(rule.getType().equals(CommonConstant.IMMEDIATE_TYPE) && memory.getMemoryUsedRate() >= rule.getThreshold()) {
                // 达到瞬时报警阈值
                String message = WarnMessageService.generateWarnMsg(memory.getHostIp(), memory.getTime().getTime(), value, rule);
                log.info("warning-bolt: immediate process, match rules:{}, message:{}", rule.toString(), message);
                ruleResult.setWarnType(CommonConstant.IMMEDIATE_TYPE);
                ruleResult.setMessage(message);
            } else if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())){
                // 进行指数移动计算
                log.info("warning-bolt: ewma process, match rules:{}, log:{}", rule.toString(), memory);
                ruleResult.setWarnType(CommonConstant.INTERVAL_TYPE);
                ruleResult.setRate(memory.getMemoryUsedRate());
                ruleResult.setIp(memory.getHostIp());
                ruleResult.setTime(memory.getTime().getTime());
            }
        }
        if(CommonConstant.SWAP.equals(rule.getKeyword())) {
            if(rule.getType().equals(CommonConstant.IMMEDIATE_TYPE) && memory.getSwapUsedRate() >= rule.getThreshold()) {
                // 达到瞬时报警阈值
                String message = WarnMessageService.generateWarnMsg(memory.getHostIp(), memory.getTime().getTime(), value, rule);
                log.info("warning-bolt: immediate process, match rules:{}, message:{}", rule.toString(), message);
                ruleResult.setWarnType(CommonConstant.IMMEDIATE_TYPE);
                ruleResult.setMessage(message);
            } else if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())){
                // 进行指数移动计算
                log.info("warning-bolt: ewma process, match rules:{}, log:{}", rule.toString(), memory);
                ruleResult.setWarnType(CommonConstant.INTERVAL_TYPE);
                ruleResult.setRate(memory.getSwapUsedRate());
                ruleResult.setIp(memory.getHostIp());
                ruleResult.setTime(memory.getTime().getTime());
            }
        }
        return ruleResult;
    }

    public static RuleResult checkHostCpuRule(Rule rule, String value) {
        RuleResult ruleResult = new RuleResult();
        CPU cpu = JSONObject.parseObject(value, CPU.class);
        if(CommonConstant.SYS_CPU.equals(rule.getKeyword())) {
            if(rule.getType().equals(CommonConstant.IMMEDIATE_TYPE) && cpu.getSystemCpu() >= rule.getThreshold()) {
                // 达到瞬时报警阈值
                String message = WarnMessageService.generateWarnMsg(cpu.getHostIp(), cpu.getTime().getTime(), value, rule);
                log.info("warning-bolt: immediate process, match rules:{}, message:{}", rule.toString(), message);
                ruleResult.setWarnType(CommonConstant.IMMEDIATE_TYPE);
                ruleResult.setMessage(message);
            } else if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())){
                // 进行指数移动计算
                log.info("warning-bolt: ewma process, match rules:{}, log:{}", rule.toString(),cpu);
                ruleResult.setWarnType(CommonConstant.INTERVAL_TYPE);
                ruleResult.setRate(cpu.getSystemCpu());
                ruleResult.setIp(cpu.getHostIp());
                ruleResult.setTime(cpu.getTime().getTime());
            }
        }
        if(CommonConstant.USER_CPU.equals(rule.getKeyword())) {
            if(rule.getType().equals(CommonConstant.IMMEDIATE_TYPE) && cpu.getUserCpu() >= rule.getThreshold()) {
                // 达到瞬时报警阈值
                String message = WarnMessageService.generateWarnMsg(cpu.getHostIp(), cpu.getTime().getTime(), value, rule);
                log.info("warning-bolt: immediate process, match rules:{}, message:{}", rule.toString(), message);
                ruleResult.setWarnType(CommonConstant.IMMEDIATE_TYPE);
                ruleResult.setMessage(message);
            } else if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())){
                // 进行指数移动计算
                log.info("warning-bolt: ewma process, match rules:{}, log:{}", rule.toString(),cpu);
                ruleResult.setWarnType(CommonConstant.INTERVAL_TYPE);
                ruleResult.setRate(cpu.getUserCpu());
                ruleResult.setIp(cpu.getHostIp());
                ruleResult.setTime(cpu.getTime().getTime());
            }
        }
        return ruleResult;
    }

    public static RuleResult checkJvmMemRule(Rule rule, String value) {
        RuleResult ruleResult = new RuleResult();
        JVMMemory jvmMemory = JSONObject.parseObject(value, JVMMemory.class);
        if(CommonConstant.JVM_MEM.equals(rule.getKeyword())) {
            if(rule.getType().equals(CommonConstant.IMMEDIATE_TYPE) && jvmMemory.getMemoryUsed() >= rule.getThreshold()) {
                // 达到瞬时报警阈值
                String message = WarnMessageService.generateWarnMsg(jvmMemory.getHostIp(), jvmMemory.getTime().getTime(), value, rule);
                log.info("warning-bolt: immediate process, match rules:{}, message:{}", rule.toString(), message);
                ruleResult.setWarnType(CommonConstant.IMMEDIATE_TYPE);
                ruleResult.setMessage(message);
            } else if(CommonConstant.INTERVAL_TYPE.equals(rule.getType())){
                // 进行指数移动计算
                log.info("warning-bolt: ewma process, match rules:{}, log:{}", rule.toString(),jvmMemory);
                ruleResult.setWarnType(CommonConstant.INTERVAL_TYPE);
                ruleResult.setRate(jvmMemory.getMemoryUsed());
                ruleResult.setIp(jvmMemory.getHostIp());
                ruleResult.setTime(jvmMemory.getTime().getTime());
            }
        }
        return ruleResult;
    }
}
