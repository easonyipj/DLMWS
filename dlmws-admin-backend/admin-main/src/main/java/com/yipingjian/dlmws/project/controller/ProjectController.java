package com.yipingjian.dlmws.project.controller;

import com.yipingjian.dlmws.project.entity.Project;
import com.yipingjian.dlmws.project.service.ProjectService;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Resource
    private ProjectService projectService;

    @PostMapping ("/add")
    public Response add(@RequestBody Project project) {
        projectService.addProject(project);
        return Response.ok();
    }
}
