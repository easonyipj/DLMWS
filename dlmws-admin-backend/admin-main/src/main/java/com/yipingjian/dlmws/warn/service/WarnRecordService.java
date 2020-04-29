package com.yipingjian.dlmws.warn.service;

import com.yipingjian.dlmws.utils.PageUtils;
import com.yipingjian.dlmws.warn.entity.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarnRecordService {
    PageUtils getWarnRecordByPage(WarnRecordVo warnRecordVo);
    List<WarnCount> getWarnCount(WarnStatisticVo warnStatisticVo, List<String> projects);
    List<TypeCount> getLogTypeCount(WarnStatisticVo warnStatisticVo, List<String> projects);
    List<TypeCountUnit> getProjectCount(WarnStatisticVo warnStatisticVo, String owner);
    List<TypeCount> getKeyWordCount(WarnStatisticVo warnStatisticVo, List<String> projects);
    List<TypeCount> getRuleTypeCount(WarnStatisticVo warnStatisticVo, List<String> projects);
    List<StatusCount> getDingStatusCount(WarnStatisticVo warnStatisticVo, List<String> projects);
    List<StatusCount> getEmailStatusCount(WarnStatisticVo warnStatisticVo, List<String> projects);
}
