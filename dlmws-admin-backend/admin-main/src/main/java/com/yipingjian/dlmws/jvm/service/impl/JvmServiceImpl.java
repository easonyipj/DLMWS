package com.yipingjian.dlmws.jvm.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.host.entity.Host;
import com.yipingjian.dlmws.host.service.HostService;
import com.yipingjian.dlmws.jvm.entity.JvmData;
import com.yipingjian.dlmws.jvm.entity.JvmEntity;
import com.yipingjian.dlmws.jvm.mapper.JvmMapper;
import com.yipingjian.dlmws.jvm.service.JvmClassService;
import com.yipingjian.dlmws.jvm.service.JvmMemoryService;
import com.yipingjian.dlmws.jvm.service.JvmService;
import com.yipingjian.dlmws.jvm.service.JvmThreadService;
import com.yipingjian.dlmws.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class JvmServiceImpl extends ServiceImpl<JvmMapper, JvmEntity> implements JvmService {

    @Resource
    private JvmThreadService jvmThreadService;
    @Resource
    private JvmClassService jvmClassService;
    @Resource
    private JvmMemoryService jvmMemoryService;
    @Resource
    private HostService hostService;

    private static final Long THREE_HUNDREDS_MIN = 24L * 60 * 60 * 1000;
    private static final String JVM_URL = "http://%s:7999/java/jps";

    @Override
    public JvmData getJvmData(String ip, int pid, long[] time) {
        JvmData jvmData = new JvmData();
        long st = time[0];
        long ed = time[1];
        try {
            if(st == 0 || ed == 0) {
                // default is 300min
                st = System.currentTimeMillis() - THREE_HUNDREDS_MIN;
                ed = System.currentTimeMillis();
            }
            // TODO  后期可以考虑用线程池优化
            jvmData.setClassList(jvmClassService.getJvmClassList(ip, pid, st, ed));
            jvmData.setMemoryList(jvmMemoryService.getJvmMemoryList(ip, pid, st, ed));
            jvmData.setThreadList(jvmThreadService.getJvmThreadList(ip, pid, st, ed));
        } catch (Exception e) {
            log.error("get jvm data error", e);
        }

        return jvmData;
    }

    /**
     * 查询用户jvm实例信息
     * @param owner 所属用户
     * @return 实例列表
     */
    @Override
    public List<JvmEntity> getJvmList(String owner) {
        List<JvmEntity> list = updateJvmList(owner);
        saveOrUpdateBatch(list);
        return list;
    }

    private List<JvmEntity> updateJvmList(String owner) {
        // 查询用户主机
        List<String> ips = hostService.getHostList(owner).stream().map(Host::getIp).collect(Collectors.toList());
        // 查询用户主机所有运行中jvm实例
        List<JvmEntity> jvmListNow = Lists.newArrayList();
        // 用户主机所有运行中jvm实例key: ip,pid
        List<String> jvmKey = Lists.newArrayList();
        ips.forEach(ip -> {
            String url = String.format(JVM_URL, ip);
            // TODO 查表获取用户主机token
            String token = "easonyisfans";
            try {
                HttpResponse response = HttpUtils.get(url, token);
                String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                List<JvmEntity> list = JSONArray.parseArray(JSONObject.parseObject(data).getJSONArray("data").toJSONString(), JvmEntity.class);
                jvmListNow.addAll(list.stream().peek(jvm -> {
                    jvm.setIp(ip);
                    jvm.setOwner(owner);
                    jvmKey.add(ip + jvm.getPid());
                }).collect(Collectors.toList()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 获取数据库中的jvm实例
        List<JvmEntity> jvmList = list(new QueryWrapper<JvmEntity>().eq("owner", owner));
        jvmList = jvmList.stream().peek(jvm -> {
            // 如果存在 则不为新产生的实例
            if(jvmKey.contains(jvm.getIp() + jvm.getPid())) {
                jvmKey.remove(jvm.getIp() + jvm.getPid());
            }else {
                // 如果不在jvmKey中, 状态修改为失联
                jvm.setStatus(0);
            }
        }).collect(Collectors.toList());

        // 新产生的实例加入到jvm list
        List<JvmEntity> newJvm = Lists.newArrayList();
        jvmListNow.forEach(jvm -> {
            if(jvmKey.contains(jvm.getIp() + jvm.getPid())) {
                // TODO agent收集 jvm info
                jvm.setInfo("");
                jvm.setAddDate(new Date(System.currentTimeMillis()));
                jvm.setStatus(1);
                newJvm.add(jvm);
            }
        });
        jvmList.addAll(newJvm);
        return jvmList;
    }
}
