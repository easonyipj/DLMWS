package com.yipingjian.dlmws;


import com.yipingjian.dlmws.tomcat.entity.EsRequestVo;
import com.yipingjian.dlmws.tomcat.service.ElasticSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AdminWebApplicationTests {
    @Resource
    private ElasticSearchService elasticSearchService;

    @Test
    public void testElasticSearchService(){
        elasticSearchService.getTomcatLogs(new EsRequestVo());
    }
}
