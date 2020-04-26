package com.yipingjian.dlmws.storm.config;

import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.storm.common.LRUMapUtil;
import com.yipingjian.dlmws.storm.entity.Rule;
import com.yipingjian.dlmws.storm.entity.RuleDTO;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.List;

@Slf4j
public class JedisPoolConfig {
    public static JedisPool jedisPool = new JedisPool(new redis.clients.jedis.JedisPoolConfig(), "127.0.0.1", 6379);
    private static final String UPDATE = "update";
    private static final String ADD = "add";
    private static final String DELETE = "delete";
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
                @SuppressWarnings("unchecked")
                public void onMessage(String channel, String message) {
                    log.info("当前线程：" + Thread.currentThread().getName() + "监听队列:" + channel + ";收到变更消息:" + message);
                    RuleDTO ruleDTO = JSONObject.parseObject(message, RuleDTO.class);
                    String project = ruleDTO.getRule().getProject();
                    String opType = ruleDTO.getOpType();
                    if(LRUMapUtil.TOMCAT_MAP.containsKey(project)) {
                        List<Rule> rules = (List<Rule>)LRUMapUtil.TOMCAT_MAP.get(project);
                        Rule newRule = ruleDTO.getRule();
                        if(ADD.equals(opType)) {
                            rules.add(newRule);
                        } else if(UPDATE.equals(opType)) {
                            for (Rule rule : rules) {
                                if(rule.getKeyword().equals(newRule.getKeyword())) {
                                    rule.setType(newRule.getType());
                                    rule.setIntervalTime(newRule.getIntervalTime());
                                    rule.setThreshold(newRule.getThreshold());
                                    rule.setDingTalkId(newRule.getDingTalkId());
                                    rule.setEmail(newRule.getEmail());
                                    rule.setOwner(newRule.getOwner());
                                    rule.setStatus(newRule.getStatus());
                                    break;
                                }
                            }
                        } else if(DELETE.equals(opType)) {
                            rules.remove(newRule);
                        }
                    }
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
