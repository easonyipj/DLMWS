package com.yipingjian.dlmws.warn.service;

import com.yipingjian.dlmws.warn.entity.Rule;

import java.util.List;

public interface RuleService {
    void addRule(String key, Rule rule);
    List<Rule> getRules(String key);
}
