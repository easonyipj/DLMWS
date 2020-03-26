package com.yipingjian.dlmws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DlmwsEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlmwsEurekaApplication.class, args);
    }

}
