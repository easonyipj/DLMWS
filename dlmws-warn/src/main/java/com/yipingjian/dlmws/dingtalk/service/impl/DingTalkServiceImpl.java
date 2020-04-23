package com.yipingjian.dlmws.dingtalk.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request.*;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.taobao.api.ApiException;
import com.yipingjian.dlmws.dingtalk.service.DingTalkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DingTalkServiceImpl implements DingTalkService {

    @Value("${dingTalk.agentId}")
    private Long AGENT_ID;

    @Value("${dingTalk.appKey}")
    private String APP_KEY;

    @Value("${dingTalk.appSecret}")
    private String APP_SECRET;

    private static final String TOKEN_API = "https://oapi.dingtalk.com/gettoken";
    private static final String MESSAGE_API = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";

    private Cache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(7000, TimeUnit.MILLISECONDS)
            .build();

    @Override
    public boolean sendDingTalkActionCardMsg(String userIdList, Markdown markdown) {
        DingTalkClient client = new DefaultDingTalkClient(MESSAGE_API);
        OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
        req.setAgentId(617017976L);
        req.setUseridList(userIdList);
        Msg msg = new Msg();
        msg.setMsgtype("markdown");
        msg.setMarkdown(markdown);
        req.setMsg(msg);
        try {
            client.execute(req, generateAccessToken());
        } catch (ApiException e) {
            log.error("send action card msg error", e);
            return false;
        }
        return true;
    }

    private String generateAccessToken() {
        // 先从cache中读取
        String token = cache.getIfPresent("token");
        // 不存在调用接口
        if(token == null) {
            token = requestAccessToken();
            // 保存到cache
            cache.put("token", token);
        }
        return token;
    }

    private String requestAccessToken() {
        String token = null;
        try {
            DingTalkClient client = new DefaultDingTalkClient(TOKEN_API);
            OapiGettokenRequest req = new OapiGettokenRequest();
            req.setHttpMethod("GET");
            req.setAppkey(APP_KEY);
            req.setAppsecret(APP_SECRET);
            OapiGettokenResponse rsp = client.execute(req);
            token = rsp.getAccessToken();
        } catch (ApiException e) {
            log.error("get access token error", e);
        }
        return token;
    }
}
