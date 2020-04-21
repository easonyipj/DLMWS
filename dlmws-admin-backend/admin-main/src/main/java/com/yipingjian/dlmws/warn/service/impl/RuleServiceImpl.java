package com.yipingjian.dlmws.warn.service.impl;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.warn.entity.Rule;
import com.yipingjian.dlmws.warn.service.RuleService;
import com.yipingjian.dlmws.warn.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class RuleServiceImpl implements RuleService {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void addRule(String key, Rule rule) {
        List<Rule> rules = redisUtil.getRules(key);
        if(rules == null) {
            rules = Lists.newArrayList();
        }
        rules.add(rule);
        redisUtil.setRule(key, rules);
    }

    @Override
    public List<Rule> getRules(String key) {
        return redisUtil.getRules(key);
    }
}
