package com.yipingjian.dlmws.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.project.entity.Project;
import com.yipingjian.dlmws.project.mapper.ProjectMapper;
import com.yipingjian.dlmws.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Override
    public void addProject(Project project) {
        List<String> ips = Lists.newArrayList(project.getIp().split(","));
        ips.forEach(ip -> {
            Project proj = new Project();
            try {
                BeanUtils.copyProperties(proj, project);
            } catch (Exception e) {
                log.error("copy bean error", e);
            }
            proj.setIp(ip);
            proj.setActiveDate(new Date(System.currentTimeMillis()));
            proj.setCreateDate(new Date(System.currentTimeMillis()));
            this.save(proj);
        });

    }

    @Override
    public String getProjectsByIp(String ip) {
        List<String> projectNames = list(new QueryWrapper<Project>().eq("ip", ip)).stream().map(Project::getName)
                .collect(Collectors.toList());
        return String.join(",", projectNames);
    }
}
