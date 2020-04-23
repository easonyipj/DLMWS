package com.yipingjian.dlmws.warn.service;

import com.yipingjian.dlmws.warn.entity.WarnMessage;

public interface WarnRecordService {
    void processWarnMessage(WarnMessage warnMessage);
}
