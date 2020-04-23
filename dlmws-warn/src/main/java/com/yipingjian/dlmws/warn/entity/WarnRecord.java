package com.yipingjian.dlmws.warn.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class WarnRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 报警项目
     */
    private String project;
    /**
     * 报警关键字
     */
    private String keyword;
    /**
     * 报警类型
     */
    private String type;
    /**
     * 阈值
     */
    private Integer threshold;
    /**
     * 时间序列周期
     */
    private Integer intervalTime;
    /**
     * dingTalkId列表
     */
    private String dingTalkId;
    /**
     * dingTalk发送状态
     * 0 用户取消
     * 1 发送成功
     * 2 发送失败
     */
    private Integer dingTalkStatus;
    /**
     * 邮件发送列表
     */
    private String email;
    /**
     * 邮件发送状态
     * 0 用户取消
     * 1 发送成功
     * 2 发送失败
     */
    private Integer emailStatus;
    /**
     * 发生时间
     */
    private Date occurredTime;
    /**
     * 报警时间
     */
    private Date warningTime;
    /**
     * 日志内容
     */
    private String logText;

    public WarnRecord(Rule rule, TomcatLogEntity tomcatLogEntity) {
        this.project = rule.getProject();
        this.keyword = rule.getKeyword();
        this.dingTalkId = rule.getDingTalkId();
        this.email = rule.getEmail();
        this.intervalTime = rule.getIntervalTime();
        this.threshold = rule.getThreshold();
        this.type = rule.getType();
        this.occurredTime = tomcatLogEntity.getOccurredTime();
        this.logText = JSONObject.toJSONString(tomcatLogEntity);
        this.dingTalkStatus = 0;
        this.emailStatus = 0;
    }
}
