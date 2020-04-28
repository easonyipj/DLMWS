package com.yipingjian.dlmws.warn.service;

import com.yipingjian.dlmws.warn.entity.Rule;

import java.util.List;

public interface RuleService {
    void addRule(Rule rule);
    void updateRule(Rule rule);
    void deleteRule(Rule rule);
    List<Rule> getRulesByKey(String key);
    List<Rule> getRulesByOwner(String owner);
    void silenceRuleById(Integer id);
}
