package com.yipingjian.dlmws.tomcat.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.tomcat.entity.*;
import com.yipingjian.dlmws.tomcat.service.TomcatLogService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.ParsedDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TomcatLogServiceImpl implements TomcatLogService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 检索日志
     * @param requestVo 请求体
     * @return 日志列表
     */
    @Override
    public List<TomcatLog> getTomcatLogs(EsRequestVo requestVo) {

        // search request builder
        SearchRequest searchRequest = new SearchRequest();

        // set the search source builder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(generateBoolQueryBuilder(requestVo));
        sourceBuilder.from(requestVo.getFrom() - 1);
        sourceBuilder.size(requestVo.getSize());
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // date desc
        sourceBuilder.sort(new FieldSortBuilder("occurredTime").order(SortOrder.DESC));
        // full the request
        searchRequest.indices(requestVo.getLogType() + "*");
        searchRequest.source(sourceBuilder);
        // execute
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("send es request error, es request:{}", searchRequest.toString(), e);
        }

        return generateTomcatLogList(searchResponse);
    }
    private BoolQueryBuilder generateBoolQueryBuilder(EsRequestVo requestVo){
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        BoolQueryBuilder errorQueryBuilder = new BoolQueryBuilder();
        if(!StringUtils.isEmpty(requestVo.getStacktrace())) {
            errorQueryBuilder.should(new MatchQueryBuilder("stacktrace", requestVo.getStacktrace()));
        }
        if(!StringUtils.isEmpty(requestVo.getErrorType())) {
            errorQueryBuilder.should(new MatchQueryBuilder("errorType", requestVo.getErrorType()));
        }
        boolQueryBuilder.must(errorQueryBuilder);
        if(!StringUtils.isEmpty(requestVo.getProject())) {
            boolQueryBuilder.must(new MatchQueryBuilder("project", requestVo.getProject()));
        }
        if(!StringUtils.isEmpty(requestVo.getLogMessage())) {
            boolQueryBuilder.must(new MatchQueryBuilder("logMessage", requestVo.getLogMessage()));
        }
        if(!StringUtils.isEmpty(requestVo.getAgentId())) {
            boolQueryBuilder.must(new MatchQueryBuilder("agentId", requestVo.getAgentId()));
        }
        if(!StringUtils.isEmpty(requestVo.getHostName())) {
            boolQueryBuilder.must(new MatchQueryBuilder("hostName", requestVo.getHostName()));
        }
        if(!StringUtils.isEmpty(requestVo.getPid())) {
            boolQueryBuilder.must(new MatchQueryBuilder("pid", requestVo.getPid()));
        }
        if(!StringUtils.isEmpty(requestVo.getThreadPosition())) {
            boolQueryBuilder.must(new MatchQueryBuilder("threadPosition", requestVo.getThreadPosition()));
        }
        if(!StringUtils.isEmpty(requestVo.getClassPosition())) {
            boolQueryBuilder.must(new MatchQueryBuilder("classPosition", requestVo.getClassPosition()));
        }
        if(!StringUtils.isEmpty(requestVo.getLevel())) {
            boolQueryBuilder.must(new MatchQueryBuilder("level", requestVo.getLevel()));
        }

        // time range
        if(!StringUtils.isEmpty(requestVo.getOccurredTime())) {
            String[] dates = requestVo.getOccurredTime().split(",");
            boolQueryBuilder.must(new RangeQueryBuilder("occurredTime")
                    .gte(dates[0])
                    .lte(dates[1]));
        }

        // 排除level为空或null的字段
        boolQueryBuilder.mustNot(
                QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("level",""))
                        .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("level"))));

        return boolQueryBuilder;
    }

    private List<TomcatLog> generateTomcatLogList(SearchResponse searchResponse) {
        List<TomcatLog> tomcatLogList = Lists.newArrayList();
        if(searchResponse == null || searchResponse.getHits() == null) {
            return tomcatLogList;
        }
        for(SearchHit searchHit : searchResponse.getHits()) {
            try {
                TomcatLog tomcatLog = JSONObject.parseObject(searchHit.getSourceAsString(), TomcatLog.class);
                tomcatLogList.add(tomcatLog);
            } catch (Exception e) {
                log.error("parse log error", e);
            }
        }
        return tomcatLogList;
    }

    /**
     * 指定时间内、按项目、以分字为周期统计日志产生数量
     * @param projects 项目列表
     * @param from 开始时间 ISO8601格式字符串
     * @param to 结束时间 （不含）ISO8601格式字符串
     * @return 项目每分钟日志产生数列表
     */
    @Override
    public List<LogCountGroupByProjectPerMin> getLogCountGroupByProjectPerMin(List<String> projects, String from, String to) {
        // search request builder
        SearchRequest searchRequest = new SearchRequest();
        // set the search source builder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(generateLogCountGroupByProjectPerMinAggBuilder(projects, from, to));
        // full the request
        searchRequest.indices("tomcat*");
        searchRequest.source(sourceBuilder);
        // execute
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("send es request error, es request:{}", searchRequest.toString(), e);
        }

        return generateLogCountGroupByProjectPerMin(searchResponse, projects);
    }

    private AggregationBuilder generateLogCountGroupByProjectPerMinAggBuilder(List<String> projects, String from, String to) {
        // date range
        DateRangeAggregationBuilder dateRangeAggregationBuilder = generateDateRangeBuilder(from, to);
        // project filter
        projects.forEach(project -> {
            FilterAggregationBuilder filterAggregationBuilder = new FilterAggregationBuilder(project, QueryBuilders.termQuery("project", project));
            DateHistogramAggregationBuilder histogramAggregationBuilder = new DateHistogramAggregationBuilder("minute");
            histogramAggregationBuilder.field("occurredTime");
            histogramAggregationBuilder.calendarInterval(DateHistogramInterval.MINUTE);
            histogramAggregationBuilder.timeZone(ZoneId.of("+08:00"));
            filterAggregationBuilder.subAggregation(histogramAggregationBuilder);
            dateRangeAggregationBuilder.subAggregation(filterAggregationBuilder);
        });
        return dateRangeAggregationBuilder;
    }

    private List<LogCountGroupByProjectPerMin> generateLogCountGroupByProjectPerMin(SearchResponse searchResponse, List<String> projects) {
        List<LogCountGroupByProjectPerMin> logCountGroupByProjectPerMinList = Lists.newArrayList();
        if(searchResponse == null || searchResponse.getAggregations() == null) {
            return logCountGroupByProjectPerMinList;
        }
        JSONObject dataRange = JSONObject.parseObject(JSONObject.toJSONString(searchResponse.getAggregations().get("dateRange")));
        JSONObject projectMaps = dataRange.getJSONArray("buckets").getJSONObject(0).getJSONObject("aggregations").getJSONObject("asMap");
        projects.forEach(project -> {
            JSONObject projectFilter = projectMaps.getJSONObject(project);
            JSONArray histogram = projectFilter.getJSONObject("aggregations").getJSONObject("asMap").getJSONObject("minute").getJSONArray("buckets");
            List<LogCountMills> logCountList = JSONArray.parseArray(histogram.toJSONString(), LogCountMills.class);
            logCountGroupByProjectPerMinList.add(new LogCountGroupByProjectPerMin(project, logCountList));
        });
        return logCountGroupByProjectPerMinList;
    }


    /**
     * 指定时间段、按项目、分等级日志数统计
     * @param projects 项目列表
     * @param from 开始时间
     * @param to 结束时间（不包含）
     * @return 项目分等级日志数统计列表
     */
    @Override
    public List<LogCountGroupByLevelPerProject> getLogCountGroupByLevelPerProject(List<String> projects, String from, String to) {
        // search request builder
        SearchRequest searchRequest = new SearchRequest();
        // set the search source builder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(generateLogCountGroupByLevelPerProjectAggBuilder(projects, from, to));
        // full the request
        searchRequest.indices("tomcat*");
        searchRequest.source(sourceBuilder);
        // execute
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("send es request error, es request:{}", searchRequest.toString(), e);
        }

        return generateLogCountGroupByLevelPerProject(searchResponse, projects);
    }

    private AggregationBuilder generateLogCountGroupByLevelPerProjectAggBuilder(List<String> projects, String from, String to) {
        // date range
        DateRangeAggregationBuilder dateRangeAggregationBuilder = generateDateRangeBuilder(from, to);
        // project filter
        projects.forEach(project -> {
            FilterAggregationBuilder filterAggregationBuilder = new FilterAggregationBuilder(project, QueryBuilders.termQuery("project", project));
            TermsAggregationBuilder termsAggregationBuilder = new TermsAggregationBuilder(project, ValueType.STRING);
            termsAggregationBuilder.field("level");
            filterAggregationBuilder.subAggregation(termsAggregationBuilder);
            dateRangeAggregationBuilder.subAggregation(filterAggregationBuilder);
        });
        return dateRangeAggregationBuilder;
    }

    private List<LogCountGroupByLevelPerProject> generateLogCountGroupByLevelPerProject(SearchResponse searchResponse, List<String> projects) {
        List<LogCountGroupByLevelPerProject> logCountGroupByLevelPerProject = Lists.newArrayList();
        if(searchResponse == null || searchResponse.getAggregations() == null) {
            return logCountGroupByLevelPerProject;
        }
        ParsedDateRange aggregation = searchResponse.getAggregations().get("dateRange");
        projects.forEach(project -> {
            ParsedFilter filter = aggregation.getBuckets().get(0).getAggregations().get(project);
            logCountGroupByLevelPerProject.add(new LogCountGroupByLevelPerProject(project, generateLogCountList(filter.getAggregations().get(project))));
        });
        return logCountGroupByLevelPerProject;
    }

    /**
     * 指定时间内、按项目 获取top[线程、类、错误类型]数
     * @param filed 待统计字段
     * @param projects 待统计项目
     * @param from 开始时间
     * @param to 结束时间（不包含）
     * @param size 统计数量（前 size 名）
     * @return 每个项目的统计情况
     */
    @Override
    public List<TopCount> getTopCount(String filed, List<String> projects, String from, String to, Integer size) {
        // search request builder
        SearchRequest searchRequest = new SearchRequest();
        // set the search source builder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(generateTopCountAggBuilder(filed, projects, from, to, size));
        // full the request
        searchRequest.indices("tomcat*");
        searchRequest.source(sourceBuilder);
        // execute
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("send es request error, es request:{}", searchRequest.toString(), e);
        }

        return generateLogCount(searchResponse, projects);
    }

    private AggregationBuilder generateTopCountAggBuilder(String field, List<String> projects, String from, String to, Integer size) {
        // date range
        DateRangeAggregationBuilder dateRangeAggregationBuilder = generateDateRangeBuilder(from, to);
        // project filter
        projects.forEach(project -> {
            FilterAggregationBuilder filterAggregationBuilder = new FilterAggregationBuilder(project, QueryBuilders.termQuery("project", project));
            TermsAggregationBuilder termsAggregationBuilder = new TermsAggregationBuilder(project, ValueType.STRING);
            termsAggregationBuilder.field(field);
            termsAggregationBuilder.size(size);
            filterAggregationBuilder.subAggregation(termsAggregationBuilder);
            dateRangeAggregationBuilder.subAggregation(filterAggregationBuilder);
        });
        return dateRangeAggregationBuilder;
    }

    private List<TopCount> generateLogCount(SearchResponse searchResponse, List<String> projects) {
        List<TopCount> topCounts = Lists.newArrayList();
        if(searchResponse == null || searchResponse.getAggregations() == null) {
            return topCounts;
        }
        ParsedDateRange aggregation = searchResponse.getAggregations().get("dateRange");
        projects.forEach(project -> {
            ParsedFilter filter = aggregation.getBuckets().get(0).getAggregations().get(project);
            topCounts.add(new TopCount(project, generateLogCountList(filter.getAggregations().get(project))));
        });
        return topCounts;
    }

    private DateRangeAggregationBuilder generateDateRangeBuilder(String from, String to) {
        // date range
        DateRangeAggregationBuilder dateRangeAggregationBuilder = new DateRangeAggregationBuilder("dateRange");
        dateRangeAggregationBuilder.field("occurredTime");
        dateRangeAggregationBuilder.addRange(from, to);
        return dateRangeAggregationBuilder;
    }

    private List<LogCount> generateLogCountList(ParsedStringTerms stringTerms) {
        List<LogCount> logCountList = Lists.newArrayList();
        stringTerms.getBuckets().forEach(bucket -> {
            LogCount logCount = new LogCount();
            logCount.setKey(bucket.getKey());
            logCount.setDocCount((int)bucket.getDocCount());
            logCountList.add(logCount);
        });
        return logCountList;
    }

}
