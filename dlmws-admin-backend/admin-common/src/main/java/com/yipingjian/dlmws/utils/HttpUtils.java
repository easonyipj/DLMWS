package com.yipingjian.dlmws.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

@Slf4j
public class HttpUtils {
    private static final HttpClient CLIENT = HttpClients.createDefault();
    public static HttpResponse get(String url, String token) throws IOException {
        HttpGet request = new HttpGet(url);
        request.setHeader("token", token);
        return CLIENT.execute(request);
    }
}
