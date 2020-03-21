package com.yipingjian.dlmws.storm.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request.Msg;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request.Text;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DingTalkService {

    private static final String BASE_URL = "https://oapi.dingtalk.com";
    private static final String TOKEN_URL = "/gettoken";
    private static final String MESSAGE_URL = "/topapi/message/corpconversation/asyncsend_v2?access_token=";
    private static final String APP_KEY = "dingqntxwywioqkbfkli";
    private static final String APP_SECRET = "EkMGdPKz3kPHnvCbSlA_BRv592KGRfFvQgmGvgFWT5EB_CmQuoyxbHoMsZw3PQ8S";
    private static final long AGENT_ID = 617017976L;

    public static void sendMessage(String message, String userList) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(BASE_URL + MESSAGE_URL + getToken());
        OapiMessageCorpconversationAsyncsendV2Request req = new OapiMessageCorpconversationAsyncsendV2Request();
        req.setAgentId(AGENT_ID);
        req.setToAllUser(true);
        Msg msg = new Msg();
        msg.setMsgtype("text");
        Text text = new Text();
        text.setContent(message);
        msg.setText(text);
        req.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(req, "f1f9b915d38935be9db9d8c2ebf541ec");
        if(rsp.isSuccess()) {
            log.info("发送消息成功, task id:{}", rsp.getTaskId());
        }else {
            log.error("发送消息异常, task id:{}, message content:{}", rsp.getTaskId(), message);
        }
    }

    private static String getToken() throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(BASE_URL + TOKEN_URL);
        OapiGettokenRequest req = new OapiGettokenRequest();
        req.setAppkey(APP_KEY);
        req.setAppsecret(APP_SECRET);
        req.setHttpMethod("GET");
        OapiGettokenResponse rsp = client.execute(req);
        System.out.println(rsp.getBody());
        return rsp.getAccessToken();
    }


}
