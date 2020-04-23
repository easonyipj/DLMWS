package com.yipingjian.dlmws.dingtalk.entity;

import lombok.Data;

@Data
public class DingResp {
    private Integer errcode;
    private String errmsg;
    private String access_token;
    private Integer expires_in;
    private Long task_id;
    private String request_id;
}
