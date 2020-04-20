package com.yipingjian.dlmws.storm.service;



import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


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
    }
}