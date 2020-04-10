package com.yipingjian.dlmws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DlmwsLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlmwsLogApplication.class, args);
    }

}
