package com.yipingjian.dlmws.warn.util;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.warn.entity.Rule;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RedisUtil {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void setRule(String key, List<Rule> rules) {
        redisTemplate.opsForValue().set(key, JSONArray.toJSONString(rules));
    }

    public List<Rule> getRules(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if(value == null) {
            return Lists.newArrayList();
        }
        return JSONArray.parseArray(value, Rule.class);
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public void push(String channel, String value) {
        redisTemplate.convertAndSend(channel, value);
    }

    public Integer getCount(String key) {
        String count = redisTemplate.opsForValue().get(key);
        if(count == null) {
            return 0;
        }
        return Integer.parseInt(count);
    }

}
