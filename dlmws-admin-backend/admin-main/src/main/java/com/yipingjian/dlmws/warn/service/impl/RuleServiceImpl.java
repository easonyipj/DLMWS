package com.yipingjian.dlmws.warn.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.warn.entity.Rule;
import com.yipingjian.dlmws.warn.entity.RuleDTO;
import com.yipingjian.dlmws.warn.mapper.RuleMapper;
import com.yipingjian.dlmws.warn.service.RuleService;
import com.yipingjian.dlmws.warn.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class RuleServiceImpl extends ServiceImpl<RuleMapper, Rule> implements RuleService {

    private static final String CHANNEL = "rule";
    private static final String UPDATE = "update";
    private static final String ADD = "add";
    private static final String DELETE = "delete";

    @Resource
    private RedisUtil redisUtil;

    @Override
    @Transactional
    public void addRule(String key, Rule rule) {
        // 检查Redis中是否已存在
        List<Rule> rules = redisUtil.getRules(key);
        if(rules == null) {
            rules = Lists.newArrayList();
        }
        // 保存到MySQL
        saveOrUpdate(rule);
        // 保存到Redis
        rules.add(rule);
        redisUtil.setRule(key, rules);
        // 通知订阅者
        redisUtil.push(CHANNEL, generatePushMessage(ADD, rule));
    }

    @Override
    public List<Rule> getRulesByKey(String key) {
        return redisUtil.getRules(key);
    }

    @Override
    @Transactional
    public void updateRule(String key, Rule rule) {
        // 保存到MySQL
        saveOrUpdate(rule);
        // 获取Key对应的rules
        List<Rule> rules = list(new QueryWrapper<Rule>().eq("project", key));
        // 保存到Redis
        redisUtil.setRule(key, rules);
        // 通知订阅者
        redisUtil.push(CHANNEL, generatePushMessage(UPDATE, rule));
    }

    @Override
    public void deleteRule(String key, Rule rule) {
        // 删除操作
        removeById(rule.getId());
        // 获取Key对应的rules
        List<Rule> rules = list(new QueryWrapper<Rule>().eq("project", key));
        if(CollectionUtils.isEmpty(rules)) {
            redisUtil.deleteKey(key);
        }else {
            // 保存到Redis
            redisUtil.setRule(key, rules);
        }
        // 通知订阅者
        redisUtil.push(CHANNEL, generatePushMessage(DELETE, rule));
    }

    @Override
    public List<Rule> getRulesByOwner(String owner) {
        return list(new QueryWrapper<Rule>().eq("owner", owner).orderByAsc("project"));
    }

    private String generatePushMessage(String opType, Rule rule) {
        RuleDTO ruleDTO = new RuleDTO();
        ruleDTO.setOpType(opType);
        ruleDTO.setRule(rule);
        return JSONObject.toJSONString(ruleDTO);
    }

}
