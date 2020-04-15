package com.yipingjian.dlmws.jvm.controller;

import com.yipingjian.dlmws.jvm.entity.JvmVo;
import com.yipingjian.dlmws.jvm.service.JvmService;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/jvm")
public class JvmController {

    @Resource
    private JvmService jvmService;

    @GetMapping("/list")
    public Response list(JvmVo jvmVo) {
        return Response.ok().put("data", jvmService.getJvmList(jvmVo.getOwner()));
    }
}
