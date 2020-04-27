package com.yipingjian.dlmws.storm.service;



import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.collections.map.LRUMap;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TomcatLogFormatServiceTest {

    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}";
    private static final String DATE_THREAD_PATTERN = "\\[\\^[*]";

    public static void main(String[] args) {
//        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);
//        JedisCommands jedisCommands = jedisPool.getResource();
//        String queue = jedisCommands.get("test");
//        System.out.println(queue);

        HashMap<String, List<String>> map = new HashMap<>();
        map.put("key1", Lists.newArrayList("value1", "value2"));
        map.put("key2", Lists.newArrayList("value1", "value2"));
        String json = JSONObject.toJSONString(map);
        System.out.println(json);
        LRUMap mapFromJson = JSONObject.parseObject(json, LRUMap.class);
        System.out.println(mapFromJson.get("key1"));
    }
}