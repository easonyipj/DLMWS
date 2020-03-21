package com.yipingjian.dlmws.storm.topology;

import com.yipingjian.dlmws.storm.bolt.LogFormatBolt;
import com.yipingjian.dlmws.storm.bolt.PersistBolt;
import com.yipingjian.dlmws.storm.bolt.WarningBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.FirstPollOffsetStrategy;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;

import java.util.Properties;

public class MyKafkaTopology {

    public static void main(String[] args) throws Exception {

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        Properties properties = new Properties();
        // kafka server的基本配置
        properties.setProperty("group.id", "test-news-topic");
        // 定义一个KafkaSpoutConfig
        KafkaSpoutConfig<String, String> kafkaSpoutConfig = KafkaSpoutConfig.builder("localhost:9092",
                "test")
                .setFirstPollOffsetStrategy(FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST)
                .setProp(properties).build();
        // KafkaSpout 实例
        KafkaSpout<String, String> kafkaSpout = new KafkaSpout<>(kafkaSpoutConfig);
        // 注入Spout
        topologyBuilder.setSpout("kafka-spout", kafkaSpout, 1);
        // 获取kafka-spout数据 进行格式化
        topologyBuilder.setBolt("log-format", new LogFormatBolt(), 1).shuffleGrouping("kafka-spout");
        // 根据配置对数据告警
        topologyBuilder.setBolt("warning-format", new WarningBolt(), 1).shuffleGrouping("log-format");
        // 持久化到es集群
        topologyBuilder.setBolt("persist-format", new PersistBolt(), 1).shuffleGrouping("warning-format");

        // 提交到storm集群
        Config config = new Config();
        config.setMessageTimeoutSecs(90);
        if (args.length > 0) { // 集群模式
            config.setDebug(false);
            StormSubmitter.submitTopology(args[0],
                    config, topologyBuilder.createTopology());
        } else {
            // 本地测试模式，一般测试使用这个
            // config.setDebug(true);
            config.setNumWorkers(2);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("local-kafka-storm-topology",
                    config, topologyBuilder.createTopology());
        }
    }
}
