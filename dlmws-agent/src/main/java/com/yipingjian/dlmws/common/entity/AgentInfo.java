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
    @Value("${client.name}")
    private String CLIENT_NAME;
    @Value("${token}")
    private String TOKEN;

    public static String STATIC_CLIENT_ID;
    public static String STATIC_CLIENT_NAME;
    public static String STATIC_TOKEN;

    @PostConstruct
    public void postConstruct() {
        STATIC_CLIENT_ID = CLIENT_ID;
        STATIC_CLIENT_NAME = CLIENT_NAME;
        STATIC_TOKEN = TOKEN;
    }
}
