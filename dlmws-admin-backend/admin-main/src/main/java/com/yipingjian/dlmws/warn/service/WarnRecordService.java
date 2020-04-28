package com.yipingjian.dlmws.warn.service;

import com.yipingjian.dlmws.utils.PageUtils;
import com.yipingjian.dlmws.warn.entity.LogTypeCount;
import com.yipingjian.dlmws.warn.entity.WarnCount;
import com.yipingjian.dlmws.warn.entity.WarnRecordVo;
import com.yipingjian.dlmws.warn.entity.WarnStatisticVo;

import java.util.List;

public interface WarnRecordService {
    PageUtils getWarnRecordByPage(WarnRecordVo warnRecordVo);
    List<WarnCount> getWarnCount(WarnStatisticVo warnStatisticVo, List<String> projects);
    List<LogTypeCount> getLogTypeCount(WarnStatisticVo warnStatisticVo, List<String> projects);
}
