package com.yipingjian.dlmws.storm.bolt;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


@Slf4j
public class PersistTomcatLogBolt extends BaseRichBolt {

    private OutputCollector outputCollector;
    private RestHighLevelClient restHighLevelClient;
    private static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        this.restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("192.168.1.110", 9200, "http")));
    }

    @Override
    public void execute(Tuple tuple) {
        String source = null;
        String logType;
        String index = null;
        try {
            source = tuple.getStringByField("value");
            logType = tuple.getStringByField("type");
            index = logType + "-" + DATA_FORMAT.format(new Date());
            IndexRequest request = new IndexRequest(index).source(JSONObject.parseObject(source));
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("持久化异常, source:\n{}, index:\n{}", source, index);
        }
        outputCollector.ack(tuple);
        log.info("persist data \n{}", source);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}