package com.yipingjian.dlmws.warn.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request.*;
import com.yipingjian.dlmws.dingtalk.service.DingTalkService;
import com.yipingjian.dlmws.mail.service.MailService;
import com.yipingjian.dlmws.warn.entity.Rule;
import com.yipingjian.dlmws.warn.entity.WarnMessage;
import com.yipingjian.dlmws.warn.entity.WarnRecord;
import com.yipingjian.dlmws.warn.mapper.WarnRecordMapper;
import com.yipingjian.dlmws.warn.service.WarnRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;


@Slf4j
@Service
public class WarnRecordServiceImpl extends ServiceImpl<WarnRecordMapper, WarnRecord> implements WarnRecordService {

    @Resource
    private DingTalkService dingTalkService;
    @Resource
    private MailService mailService;

    private static final int SUCCESS = 1;
    private static final int ERROR = 2;
    private static final String SUBJECT = "邮件报警通知";
    private static final String SILENCE_LINK = "[静默](http://192.168.1.110:8088/rule/silence?id=%s)";

    @Override
    public void processWarnMessage(WarnMessage warnMessage) {
        Rule rule = warnMessage.getRule();
        WarnRecord warnRecord = new WarnRecord(rule, warnMessage.getIp(), warnMessage.getOccurredTime(), warnMessage.getLogText());
        try {
            if(rule.getStatus() && !StringUtils.isEmpty(rule.getDingTalkId())) {
                warnRecord.setDingTalkStatus(sendDingTalk(rule, warnMessage.getOccurredTime()));
            }
            if(rule.getStatus() && !StringUtils.isEmpty(rule.getEmail())) {
                warnRecord.setEmailStatus(sendMail(rule, warnMessage.getOccurredTime(), warnMessage.getLogText()));
            }
            // 入库
            warnRecord.setWarningTime(new Date(System.currentTimeMillis()));
            saveOrUpdate(warnRecord);
        } catch (Exception e) {
            log.error("process warning message error\n rule:\n{}, log:\n{}", rule, warnMessage.getLogText(), e);
        }
    }

    private Integer sendDingTalk(Rule rule, long occurredTime) {
        if(dingTalkService.sendDingTalkActionCardMsg(rule.getDingTalkId(), generateMarkdown(rule, occurredTime))) {
            return SUCCESS;
        }else {
            return ERROR;
        }
    }

    private Integer sendMail(Rule rule, long occurredTime, String logText) {
        String text = mailService.generateMailHtmlText(rule, occurredTime, logText);
        String[] to = rule.getEmail().split(",");
        if(mailService.sendHtmlMail(SUBJECT, to, text)) {
            return SUCCESS;
        }else {
            return ERROR;
        }
    }

    private Markdown generateMarkdown(Rule rule, long occurredTime) {
        Markdown markdownBody = new Markdown();
        markdownBody.setTitle("实时报警推送");
        StringBuilder markdown = new StringBuilder();
        markdown.append("**实时报警推送** ").append("  \n  ");
        markdown.append("**项目** ").append(rule.getProject()).append("  \n  ");
        markdown.append("**关键字** ").append(rule.getKeyword()).append("  \n  ");
        markdown.append("**报警类型** ").append(rule.getType()).append("  \n  ");
        if(rule.getType().equals("interval")) {
            markdown.append("**阈值** ").append(rule.getThreshold()).append("  \n  ");
            markdown.append("**周期** ").append(rule.getIntervalTime()).append("  \n  ");
        }
        markdown.append("**发生时间** ").append(new Date(occurredTime).toString()).append("  \n  ");
        markdown.append("[查看](http://192.168.1.110:9528/#/)  ").append(String.format(SILENCE_LINK, rule.getId()));
        markdownBody.setText(markdown.toString());
        return markdownBody;
    }

//    private ActionCard generateActionCard(Rule rule, Date occurredTime) {
//        ActionCard actionCard = new ActionCard();
//        actionCard.setTitle("实时报警推送");
//        StringBuilder markdown = new StringBuilder();
//        markdown.append("**实时报警推送**").append("  \n  ");
//        markdown.append("**项目:**").append(rule.getProject()).append("  \n  ");
//        markdown.append("**关键字:**").append(rule.getKeyword()).append("  \n  ");
//        markdown.append("**报警类型:**").append(rule.getType()).append("  \n  ");
//        if(rule.getType().equals("interval")) {
//            markdown.append("**阈值:**").append(rule.getThreshold()).append("  \n  ");
//            markdown.append("**周期:**").append(rule.getIntervalTime()).append("  \n  ");
//        }
//        markdown.append("**发生时间:**").append(occurredTime.toString()).append("  \n  ");
//        actionCard.setMarkdown(markdown.toString());
//        actionCard.setBtnOrientation("1");
//        BtnJsonList checkBtn = new BtnJsonList();
//        checkBtn.setTitle("查看");
//        checkBtn.setActionUrl("xxxx");
//        BtnJsonList silenceBtn = new BtnJsonList();
//        silenceBtn.setTitle("静默");
//        silenceBtn.setActionUrl("xxxx");
//        List<BtnJsonList> btnJsonLists = Lists.newArrayList(checkBtn, silenceBtn);
//        actionCard.setBtnJsonList(btnJsonLists);
//        return actionCard;
//    }

}
