package com.yipingjian.dlmws.host.service.impl;

import com.yipingjian.dlmws.host.service.HostService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class HostServiceImplTest {
    @Resource
    private HostService hostService;

    @Test
    void getHostBasicInfo() {
        String result = hostService.getHostBasicInfo();
        System.out.println(result);
    }
}