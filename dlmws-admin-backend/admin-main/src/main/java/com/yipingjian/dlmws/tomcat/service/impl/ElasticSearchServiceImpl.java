package com.yipingjian.dlmws.tomcat.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yipingjian.dlmws.tomcat.entity.EsRequestVo;
import com.yipingjian.dlmws.tomcat.entity.TomcatLog;
import com.yipingjian.dlmws.tomcat.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

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
