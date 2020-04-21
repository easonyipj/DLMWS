package com.yipingjian.dlmws;


import com.yipingjian.dlmws.tomcat.entity.EsRequestVo;
import com.yipingjian.dlmws.tomcat.service.TomcatLogService;
import com.yipingjian.dlmws.warn.entity.Rule;
import com.yipingjian.dlmws.warn.service.RuleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AdminWebApplicationTests {
    @Resource
    private TomcatLogService elasticSearchService;

    @Resource
    private RuleService ruleService;

    @Test
    public void testElasticSearchService(){
        elasticSearchService.getTomcatLogs(new EsRequestVo());
    }

    @Test
    public void testRuleService() {
        ruleService.addRule("test", new Rule());
    }
}
