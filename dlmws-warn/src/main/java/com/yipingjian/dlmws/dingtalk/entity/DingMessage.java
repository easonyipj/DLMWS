package com.yipingjian.dlmws.dingtalk.entity;

import lombok.Data;

@Data
public class DingMessage {
    private String msgtype;
    private ActionCard action_card;
}
