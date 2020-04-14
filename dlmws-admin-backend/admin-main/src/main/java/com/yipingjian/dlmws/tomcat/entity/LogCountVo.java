package com.yipingjian.dlmws.tomcat.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class LogCountVo {
    private static final long serialVersionUID = 1L;
    private String type;
    private Integer size;
    private String projects;
    private String from;
    private String to;
}
