package com.yipingjian.dlmws.host.controller;


import com.alibaba.fastjson.JSONObject;
import com.yipingjian.dlmws.common.utils.Response;
import com.yipingjian.dlmws.host.entity.HostBasicInfo;
import com.yipingjian.dlmws.host.service.HostService;
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
    private HostService hostService;

    @GetMapping("/basicInfo")
    public Response basicInfo() {
        try {
            HostBasicInfo hostBasicInfo = hostService.getHostBasicInfo();
            return Response.ok().put("data", JSONObject.toJSONString(hostBasicInfo));
        } catch (Exception e) {
            log.error("get basic info exception", e);
            return Response.error();
        }
    }


}
