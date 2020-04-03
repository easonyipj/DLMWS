package com.yipingjian.dlmws.common.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
public class AgentInfo {
    @Value("${client.id}")
    private String CLIENT_ID;

    public static String STATIC_CLIENT_ID;

    @PostConstruct
    public void getClientId() {
        STATIC_CLIENT_ID = CLIENT_ID;
    }
}
