package com.yipingjian.dlmws;

import com.dingtalk.api.request.OapiMessageSendToConversationRequest;
import com.yipingjian.dlmws.dingtalk.service.DingTalkService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class DlmwsWarnApplicationTests {

    @Resource
    private DingTalkService dingTalkService;

    @Test
    void contextLoads() {
        OapiMessageSendToConversationRequest.ActionCard actionCard = new OapiMessageSendToConversationRequest.ActionCard();
        //dingTalkService.sendDingTalkActionCardMsg("224456436025885754", actionCard);
    }

}
