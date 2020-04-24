package com.yipingjian.dlmws.warn.service;

import com.yipingjian.dlmws.utils.PageUtils;
import com.yipingjian.dlmws.warn.entity.WarnRecordVo;

public interface WarnRecordService {
    PageUtils getWarnRecordByPage(WarnRecordVo warnRecordVo);
}
