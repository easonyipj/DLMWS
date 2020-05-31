package com.yipingjian.dlmws.host.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.host.entity.AddHostVo;
import com.yipingjian.dlmws.host.entity.Host;
import com.yipingjian.dlmws.host.service.HostCpuService;
import com.yipingjian.dlmws.host.service.HostService;
import com.yipingjian.dlmws.project.entity.Project;
import com.yipingjian.dlmws.project.service.ProjectService;
import com.yipingjian.dlmws.tomcat.entity.LogCountVo;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/host")
public class HostController {

    @Resource
    private HostService hostService;

    @Resource
    private ProjectService projectService;

    @GetMapping("/list")
    public Response list(@RequestParam(name = "owner")String owner) {
        return Response.ok().put("data", hostService.getHostList(owner));
    }

    @PostMapping("/add")
    public Response add(@RequestBody AddHostVo addHostVo) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "http://" + addHostVo.getIp() + ":7999/host/basicInfo";
        HttpGet httpGet = new HttpGet(url);
        String projects = projectService.getProjectsByIp(addHostVo.getIp());
        httpGet.setHeader("token", addHostVo.getSecret());
        HttpResponse httpResponse = httpClient.execute(httpGet);
        String result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
        JSONObject jsonObject = JSONObject.parseObject(result);
        if(jsonObject.getInteger("code") == 401) {
            return Response.error("密钥错误");
        }
        if(jsonObject.getInteger("code") == 200) {
            Host host = new Host();
            host.setInfo(jsonObject.getString("data"));
            host.setName(addHostVo.getName());
            host.setOwner("yipingjian");
            host.setStatus((short) 1);
            host.setIp(addHostVo.getIp());
            host.setProjects(projects);
            hostService.addHost(host);
            return Response.ok();
        }
        return Response.error("未知异常");
    }
}
