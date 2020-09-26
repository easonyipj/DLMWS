package com.yipingjian.dlmws.storm.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.storm.config.JedisPoolConfig;
import com.yipingjian.dlmws.storm.entity.Rule;
import org.apache.commons.collections.map.LRUMap;
import redis.clients.jedis.commands.JedisCommands;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUMapUtil {
    public static LRUMap PROJECT_MAP;
    public static LRUMap TOMCAT_MAP = new LRUMap();
    public static LRUMap HOST_MEM_MAP = new LRUMap();
    public static LRUMap HOST_CPU_MAP = new LRUMap();
    public static LRUMap JVM_MEM_MAP = new LRUMap();
    public static LRUMap JVM_THREAD_MAP = new LRUMap();
    public static JedisCommands jedisCommands = JedisPoolConfig.jedisPool.getResource();
    private static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();


    static {
        String jsonMap = jedisCommands.get("project-rules");
        PROJECT_MAP = JSONObject.parseObject(jsonMap, LRUMap.class);
    }

    @SuppressWarnings("unchecked")
    public static List<Rule> getRules(String project, String ruleType) {

        LRUMap ruleMap = getRuleMapByType(ruleType);
        // 查看map中是否包含project
        if(!ruleMap.containsKey(project)) {
            // TODO 加写锁
            String ruleString = jedisCommands.get(project + ":" + ruleType);
            List<Rule> rules = JSONArray.parseArray(ruleString, Rule.class);
            // 此种情况正常不会发生
            if(rules == null) {
                rules = Lists.newArrayList();
            }
            ruleMap.put(project, rules);
        }
        // TODO 加读锁
        return (List<Rule>)ruleMap.get(project);
    }

    public static LRUMap getRuleMapByType(String ruleType) {
        switch (ruleType) {
            case CommonConstant.HOST_MEM:
                return HOST_MEM_MAP;
            case CommonConstant.HOST_CPU:
                return HOST_CPU_MAP;
            case CommonConstant.JVM_MEM:
                return JVM_MEM_MAP;
            case CommonConstant.JVM_THREAD:
                return JVM_THREAD_MAP;
            default:
                return TOMCAT_MAP;
        }
    }


}
