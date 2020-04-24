package com.yipingjian.dlmws.warn.service;


import com.yipingjian.dlmws.utils.PageUtils;
import com.yipingjian.dlmws.warn.entity.Rule;

import java.util.List;

public interface RuleService {
    void addRule(String key, Rule rule);
    void updateRule(String key, Rule rule);
    void deleteRule(String key, Rule rule);
    List<Rule> getRulesByKey(String key);
    List<Rule> getRulesByOwner(String owner);
    void silenceRuleById(Integer id);
}
