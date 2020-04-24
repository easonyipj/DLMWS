package com.yipingjian.dlmws.warn.controller;

import com.yipingjian.dlmws.utils.PageUtils;
import com.yipingjian.dlmws.utils.Response;
import com.yipingjian.dlmws.warn.entity.WarnRecordVo;
import com.yipingjian.dlmws.warn.service.WarnRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/warn")
public class WarnRecordController {

    @Resource
    private WarnRecordService warnRecordService;

    @GetMapping("/list")
    public Response list(WarnRecordVo warnRecordVo) {
        PageUtils pageUtils = null;
        // TODO 获取登录用户名
        String owner = "yipingjian";
        warnRecordVo.setOwner(owner);
        try {
            pageUtils = warnRecordService.getWarnRecordByPage(warnRecordVo);
        } catch (Exception e) {
            log.error("get warn record list error", e);
        }
        return Response.ok().put("data", pageUtils);
    }
}
