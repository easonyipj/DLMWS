package com.yipingjian.dlmws.java.controller;

import com.yipingjian.dlmws.common.utils.Response;
import com.yipingjian.dlmws.java.entity.JPS;
import com.yipingjian.dlmws.java.service.JavaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/java")
public class JavaController {

    @Resource
    private JavaService javaService;

    @GetMapping("/jps")
    public Response jps() {
        try {
            List<JPS> jpsList = javaService.getJPSInfo();
            return Response.ok().put("data", jpsList);
        } catch (Exception e) {
            log.error("jps error", e);
            return Response.error();
        }
    }

}
