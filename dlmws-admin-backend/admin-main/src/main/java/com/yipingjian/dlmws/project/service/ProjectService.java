package com.yipingjian.dlmws.project.service;

import com.yipingjian.dlmws.project.entity.Project;

import java.util.List;

public interface ProjectService {
    void addProject(Project project);
    String getProjectsByIp(String ip);
}
