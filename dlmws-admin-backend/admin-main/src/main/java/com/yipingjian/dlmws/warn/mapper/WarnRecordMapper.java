package com.yipingjian.dlmws.warn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yipingjian.dlmws.warn.entity.LogTypeCountUnit;
import com.yipingjian.dlmws.warn.entity.WarnCountUnit;
import com.yipingjian.dlmws.warn.entity.WarnRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WarnRecordMapper extends BaseMapper<WarnRecord> {
    @Select("<script>"
            +"select warning_time as time, count(*) as count from warn_record where project = #{project} "
            +"<if test='from != null and to != null'>"
            +"and warning_time between #{from} and #{to} "
            +"</if>"
            +"group by warning_time"
            +"</script>")
    List<WarnCountUnit> getWarnCount(@Param("project") String project, @Param("from") String from, @Param("to") String to);

    @Select("<script>"
            +"select log_type,count(*) as count from warn_record where project = #{project} "
            +"<if test='from != null and to != null'>"
            +"and warning_time between #{from} and #{to} "
            +"</if>"
            +"group by log_type"
            +"</script>")
    List<LogTypeCountUnit> getLogTypeCount(@Param("project") String project, @Param("from") String from, @Param("to") String to);
}
