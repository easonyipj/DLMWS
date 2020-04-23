package com.yipingjian.dlmws.mail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Mail {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String template;
}
