package com.yipingjian.dlmws.mail.service;
import com.yipingjian.dlmws.warn.entity.Rule;
import com.yipingjian.dlmws.warn.entity.TomcatLogEntity;

public interface MailService {
    boolean sendHtmlMail(String subject, String[] to, String html);
    String generateMailHtmlText(Rule rule , TomcatLogEntity tomcatLogEntity);
}
