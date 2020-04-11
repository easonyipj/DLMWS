package com.yipingjian.dlmws.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ElasticSearchClientConfig {

    @Value("${es.hostname}")
    private String hostname;

    @Value("${es.port}")
    private Integer port;

    @Value("${es.scheme}")
    private String scheme;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(hostname, port, scheme)));
    }
}
