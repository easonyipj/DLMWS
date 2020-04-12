package com.yipingjian.dlmws.tomcat.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.tomcat.entity.EsRequestVo;
import com.yipingjian.dlmws.tomcat.entity.LogCountGroupByProjectPerMin;
import com.yipingjian.dlmws.tomcat.entity.LogCountPerMin;
import com.yipingjian.dlmws.tomcat.entity.TomcatLog;
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
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TomcatLogServiceImpl implements TomcatLogService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

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

    @Override
    public List<LogCountGroupByProjectPerMin> getLogCountGroupByProjectPerMin(List<String> projects, Date from, Date to) {
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

    private AggregationBuilder generateLogCountGroupByProjectPerMinAggBuilder(List<String> projects, Date from, Date to) {
        // date range
        DateRangeAggregationBuilder dateRangeAggregationBuilder = new DateRangeAggregationBuilder("dateRange");
        dateRangeAggregationBuilder.field("occurredTime");
        // TODO 时间格式化成ISO标准格式
        dateRangeAggregationBuilder.addRange("2020-04-11T22:41:00.000+08:00", "2020-04-11T22:50:00.000+08:00");
        // project filter
        projects.forEach(project -> {
            FilterAggregationBuilder filterAggregationBuilder = new FilterAggregationBuilder(project, QueryBuilders.termQuery("project", project));
            DateHistogramAggregationBuilder histogramAggregationBuilder = new DateHistogramAggregationBuilder("minute");
            histogramAggregationBuilder.field("occurredTime");
            histogramAggregationBuilder.calendarInterval(DateHistogramInterval.MINUTE);
            histogramAggregationBuilder.timeZone(ZoneId.of("Asia/Shanghai"));
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
            List<LogCountPerMin> logCountPerMinList = JSONArray.parseArray(histogram.toJSONString(), LogCountPerMin.class);
            logCountGroupByProjectPerMinList.add(new LogCountGroupByProjectPerMin(project, logCountPerMinList));
        });
        return logCountGroupByProjectPerMinList;
    }

    private BoolQueryBuilder generateBoolQueryBuilder(EsRequestVo requestVo){
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
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
        if(!StringUtils.isEmpty(requestVo.getStacktrace())) {
            boolQueryBuilder.must(new MatchQueryBuilder("stacktrace", requestVo.getStacktrace()));
        }
        if(!StringUtils.isEmpty(requestVo.getErrorType())) {
            boolQueryBuilder.must(new MatchQueryBuilder("errorType", requestVo.getErrorType()));
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

}
