package com.yipingjian.dlmws.storm.config;

import redis.clients.jedis.JedisPool;

public class JedisPoolConfig {
    public static JedisPool jedisPool;
    static {
        jedisPool = new JedisPool(new redis.clients.jedis.JedisPoolConfig(), "127.0.0.1", 6379);
    }
}
