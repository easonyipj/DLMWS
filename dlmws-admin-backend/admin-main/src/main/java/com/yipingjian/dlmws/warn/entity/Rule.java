package com.yipingjian.dlmws.warn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Rule {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String project;
    private String logType;
    private String keyword;
    private String type;
    private Integer threshold;
    private Integer intervalTime;
    private String dingTalkId;
    private String email;
    private Boolean status;
    private String owner;
}
