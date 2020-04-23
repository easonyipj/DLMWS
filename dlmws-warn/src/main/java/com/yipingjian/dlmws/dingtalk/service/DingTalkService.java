package com.yipingjian.dlmws.dingtalk.service;

import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;


public interface DingTalkService {
    boolean sendDingTalkActionCardMsg(String userIdList, OapiMessageCorpconversationAsyncsendV2Request.Markdown markdown);
}
