package com.yipingjian.dlmws;

import com.yipingjian.dlmws.host.service.HostService;
import com.yipingjian.dlmws.java.service.JavaService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class DlmwsApplicationTests {

    @Resource
    private HostService hostService;

    @Resource
    private JavaService javaService;

    @Test
    public void test() throws Exception {
        System.out.println(javaService.getJPSInfo());
    }

    @Test
    public void testHostService() throws Exception{
        System.out.println(hostService.getCPULoad());
    }

}
