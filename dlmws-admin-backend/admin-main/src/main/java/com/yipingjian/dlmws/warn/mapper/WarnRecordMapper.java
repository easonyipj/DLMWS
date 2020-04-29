package com.yipingjian.dlmws.warn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yipingjian.dlmws.warn.entity.StatusCountUnit;
import com.yipingjian.dlmws.warn.entity.TypeCountUnit;
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
    List<TypeCountUnit> getLogTypeCount(@Param("project") String project, @Param("from") String from, @Param("to") String to);

    @Select("<script>"
            +"select project as log_type,count(*) as count from warn_record where owner = #{owner} "
            +"<if test='from != null and to != null'>"
            +"and warning_time between #{from} and #{to} "
            +"</if>"
            +"group by project"
            +"</script>")
    List<TypeCountUnit> getProjectCount(@Param("owner") String owner, @Param("from") String from, @Param("to") String to);

    @Select("<script>"
            +"select keyword as log_type,count(*) as count from warn_record where project = #{project} "
            +"<if test='from != null and to != null'>"
            +"and warning_time between #{from} and #{to} "
            +"</if>"
            +"group by keyword"
            +"</script>")
    List<TypeCountUnit> getKeyWordCount(@Param("project") String project, @Param("from") String from, @Param("to") String to);

    @Select("<script>"
            +"select type as log_type,count(*) as count from warn_record where project = #{project} "
            +"<if test='from != null and to != null'>"
            +"and warning_time between #{from} and #{to} "
            +"</if>"
            +"group by type"
            +"</script>")
    List<TypeCountUnit> getRuleTypeCount(@Param("project") String project, @Param("from") String from, @Param("to") String to);

    @Select("<script>"
            +"select ding_talk_status as code,count(*) as count from warn_record where project = #{project} "
            +"<if test='from != null and to != null'>"
            +"and warning_time between #{from} and #{to} "
            +"</if>"
            +"group by ding_talk_status"
            +"</script>")
    List<StatusCountUnit> getDingWarnStatusCount(@Param("project") String project, @Param("from") String from, @Param("to") String to);

    @Select("<script>"
            +"select email_status as code,count(*) as count from warn_record where project = #{project} "
            +"<if test='from != null and to != null'>"
            +"and warning_time between #{from} and #{to} "
            +"</if>"
            +"group by email_status"
            +"</script>")
    List<StatusCountUnit> getEmailWarnStatusCount(@Param("project") String project, @Param("from") String from, @Param("to") String to);
}
