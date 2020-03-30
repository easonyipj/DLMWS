package com.yipingjian.dlmws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;

@EnableConfigurationProperties
@SpringBootApplication
@ServletComponentScan
public class DLMWSAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DLMWSAgentApplication.class, args);
    }

}
