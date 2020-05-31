package com.yipingjian.dlmws.host.entity;

import lombok.Data;

@Data
public class AddHostVo {
    private String ip;
    private String name;
    private String secret;
}
