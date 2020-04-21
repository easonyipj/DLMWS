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
        ruleService.addRule(rule.getProject(), rule);
        return Response.ok();
    }

    @GetMapping("/get")
    public Response getRules(@RequestParam(name = "key") String key) {
        return Response.ok().put("data", ruleService.getRules(key));
    }
}
