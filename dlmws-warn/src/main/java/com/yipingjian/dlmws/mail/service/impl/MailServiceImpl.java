package com.yipingjian.dlmws.mail.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yipingjian.dlmws.mail.entity.Mail;
import com.yipingjian.dlmws.mail.mapper.MailMapper;
import com.yipingjian.dlmws.mail.service.MailService;
import com.yipingjian.dlmws.warn.entity.Rule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;


@Slf4j
@Service
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements MailService {

    @Value("${spring.mail.username}")
    private String mailUserName;
    @Value("${mail.personal}")
    private String personal;

    @Resource
    private JavaMailSender mailSender;


    @Override
    public boolean sendHtmlMail(String subject, String[] to, String html) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setSubject(subject);
            // TODO 发件人别名中文乱码
            mimeMessageHelper.setFrom(new InternetAddress(mailUserName, personal, null));
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setTo(to);
            // mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("send mail error", e);
            return false;
        }
        return true;
    }

    @Override
    public String generateMailHtmlText(Rule rule, long occurredTime, String logText) {
        String text = this.getOne(new QueryWrapper<Mail>().eq("name", "tomcat")).getTemplate();
        text = text.replace("{{owner}}", rule.getOwner());
        text = text.replace("{{project}}", rule.getProject());
        text = text.replace("{{keyword}}", rule.getKeyword());
        text = text.replace("{{type}}", rule.getType());
        text = text.replace("{{threshold}}", String.valueOf(rule.getThreshold()));
        text = text.replace("{{intervalTime}}", String.valueOf(rule.getIntervalTime()));
        text = text.replace("{{occurredTime}}", new Date(occurredTime).toString());
        text = text.replace("{{log}}", logText);
        return text;
    }

//    public InternetAddress getFromInternetAddress(String from) {
//        String personal = null;
//        String address = null;
//
//        if(from.matches(regex1)){
//            personal = from.replaceAll(regex2, "").trim();
//            Matcher m = Pattern.compile(regex2).matcher(from);
//            if(m.find()){
//                address = m.group(1).trim();
//            }
//            try {
//                return new InternetAddress(address, personal, "gb2312");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }else{
//            try {
//                return new InternetAddress(from);
//            } catch (AddressException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

}
