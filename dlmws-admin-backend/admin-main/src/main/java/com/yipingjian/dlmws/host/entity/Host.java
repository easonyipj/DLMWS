package com.yipingjian.dlmws.host.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class Host implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String ip;
    private String name;
    private String projects;
    private String owner;
    private String info;
    private Short status;
}
