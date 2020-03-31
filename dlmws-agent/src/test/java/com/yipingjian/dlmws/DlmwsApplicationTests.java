package com.yipingjian.dlmws;

import com.yipingjian.dlmws.host.service.HostService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class DlmwsApplicationTests {

    @Resource
    private HostService hostService;

    @Test
    public void test() {
        System.out.println(hostService.getMemoryLoad());
        System.out.println(hostService.getCPULoad());
    }

}
