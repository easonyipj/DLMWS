package com.yipingjian.dlmws.host.controller;

import com.yipingjian.dlmws.host.service.HostCpuService;
import com.yipingjian.dlmws.host.service.HostService;
import com.yipingjian.dlmws.tomcat.entity.LogCountVo;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/host")
public class HostController {

    @Resource
    private HostService hostService;

    @GetMapping("/list")
    public Response list(@RequestParam(name = "owner")String owner) {
        return Response.ok().put("data", hostService.getHostList(owner));
    }
}
