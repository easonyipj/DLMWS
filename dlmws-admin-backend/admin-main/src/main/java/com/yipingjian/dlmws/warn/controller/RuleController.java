package com.yipingjian.dlmws.warn.controller;

import com.yipingjian.dlmws.utils.Response;
import com.yipingjian.dlmws.warn.entity.Rule;
import com.yipingjian.dlmws.warn.service.RuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/rule")
public class RuleController {

    @Resource
    private RuleService ruleService;

    @PostMapping("/add")
    public Response addRule(@RequestBody Rule rule) {
        // TODO 获取当前owner
        try {
            rule.setOwner("yipingjian");
            ruleService.addRule(rule);
        } catch (Exception e) {
            log.error("add rule error", e);
            return Response.error();
        }
        return Response.ok();
    }

    @GetMapping("/getByKey")
    public Response getRulesByKey(@RequestParam(name = "key") String key) {
        return Response.ok().put("data", ruleService.getRulesByKey(key));
    }

    @GetMapping("/getByOwner")
    public Response getRulesByOwner() {
        // TODO 获取当前owner
        String owner = "yipingjian";
        return Response.ok().put("data", ruleService.getRulesByOwner(owner));
    }

    @PostMapping("/update")
    public Response update(@RequestBody Rule rule) {
        ruleService.updateRule(rule);
        return Response.ok();
    }

    @PostMapping("/delete")
    public Response delete(@RequestBody Rule rule) {
        ruleService.deleteRule(rule);
        return Response.ok();
    }

    @GetMapping("/silence")
    public Response silence(@RequestParam(name = "id")Integer id){
        ruleService.silenceRuleById(id);
        return Response.ok();
    }
}
