package com.yipingjian.dlmws.jvm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "jvm")
public class JvmEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String ip;
    private Integer pid;
    private String name;
    private Date addDate;
    private String owner;
    private String info;
    private Integer status;
}
