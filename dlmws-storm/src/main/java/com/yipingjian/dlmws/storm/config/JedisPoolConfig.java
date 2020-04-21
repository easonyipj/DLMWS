package com.yipingjian.dlmws.storm.config;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

@Slf4j
public class JedisPoolConfig {
    public static JedisPool jedisPool = new JedisPool(new redis.clients.jedis.JedisPoolConfig(), "127.0.0.1", 6379);;
    static class ListenerThread extends Thread{
        JedisPool pool;
        String channel;
        public ListenerThread(JedisPool pool, String channel) {
            this.pool = pool;
            this.channel = channel;
        }
        @Override
        public void run() {
            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    log.info("当前线程：" + Thread.currentThread().getName() + "监听队列:" + channel + ";收到变更消息:" + message);
                }
            };
            try (Jedis jedis = pool.getResource()) {
                jedis.subscribe(jedisPubSub, channel);
            }
        }

    }
    static {
        ListenerThread listenerThread = new ListenerThread(jedisPool, "rule");
        listenerThread.start();
    }

}
