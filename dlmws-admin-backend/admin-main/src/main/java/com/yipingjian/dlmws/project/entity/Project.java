package com.yipingjian.dlmws.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Project {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String ip;
    private String secret;
    private String description;
    private Date createDate;
    private Date activeDate;
}
