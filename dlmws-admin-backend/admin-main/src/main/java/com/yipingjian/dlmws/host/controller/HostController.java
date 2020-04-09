package com.yipingjian.dlmws.host.controller;

import com.yipingjian.dlmws.host.service.HostCpuService;
import com.yipingjian.dlmws.host.service.HostService;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/host")
public class HostController {

    @Resource
    private HostCpuService hostCpuService;
    @Resource
    private HostService hostService;

    @GetMapping("/cpuList")
    public Response cpuList() {
        return Response.ok().put("data", hostService.getHostData("192.168.0.19", new long[2]));
    }


}
