package com.yipingjian.dlmws.common.utils;

import com.yipingjian.dlmws.common.constant.HTTPResponseConstant;

import java.util.HashMap;

public class Response extends HashMap<String, Object> {
    private static final long serialVersionUID = 1874L;


    public static Response ok() {
        Response response = new Response();
        response.put("code", HTTPResponseConstant.OK_CODE);
        response.put("msg", HTTPResponseConstant.OK_INFO);
        return response;
    }

    public static Response ok(String msg) {
        Response response = new Response();
        response.put("code", HTTPResponseConstant.OK_CODE);
        response.put("msg", msg);
        return response;
    }

    public static Response error(int code, String msg) {
        Response response = new Response();
        response.put("code", code);
        response.put("msg", msg);
        return response;
    }

    public static Response error(String msg) {
        return error(HTTPResponseConstant.ERROR_CODE, msg);
    }

    public static Response error() {
        return error(HTTPResponseConstant.ERROR_CODE, HTTPResponseConstant.ERROR_INFO);
    }

    public static Response unAuth(String msg) {
        Response response = new Response();
        response.put("code", HTTPResponseConstant.UN_AUTH_CODE);
        response.put("msg", msg);
        return response;
    }

    @Override
    public Response put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
