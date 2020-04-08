package com.yipingjian.dlmws.storm.topology;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.yipingjian.dlmws.storm.bolt.*;
import com.yipingjian.dlmws.storm.common.CommonConstant;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.FirstPollOffsetStrategy;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;

import java.util.HashMap;
import java.util.Properties;

public class DLMWSTopology {

    public static void main(String[] args) throws Exception {

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        Properties properties = new Properties();
        // kafka server的基本配置
        properties.setProperty("group.id", "test-news-topic");
        // 定义一个KafkaSpoutConfig
        KafkaSpoutConfig<String, String> kafkaSpoutConfig = KafkaSpoutConfig.builder("localhost:9092",
                "tomcat", "host-cpu", "host-mem", "jvm-mem", "jvm-thread", "jvm-class")
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
        // 分发bolt
        topologyBuilder.setBolt("distribute", new DistributeBolt(), 1).shuffleGrouping("warning-format");
        // 持久化tomcat log到es集群
        topologyBuilder.setBolt("persist-tomcat-log", new PersistTomcatLogBolt(), 1)
                .localOrShuffleGrouping("distribute", CommonConstant.TOMCAT);
        // 持久化host log到 mysql
        topologyBuilder.setBolt("persist-host-mem", PersistToMySQLBoltBuilder.createHostMemInsertBolt(), 1)
                .localOrShuffleGrouping("distribute", CommonConstant.HOST_MEM);
        topologyBuilder.setBolt("persist-host-cpu", PersistToMySQLBoltBuilder.createHostCpuInsertBolt(), 1)
                .localOrShuffleGrouping("distribute", CommonConstant.HOST_CPU);
        // 持久化jvm log到mysql
        topologyBuilder.setBolt("persist-jvm-mem", PersistToMySQLBoltBuilder.createJvmMemInsertBolt(), 1)
                .localOrShuffleGrouping("distribute", CommonConstant.JVM_MEM);
        topologyBuilder.setBolt("persist-jvm-thread", PersistToMySQLBoltBuilder.createJvmThreadInsertBolt(), 1)
                .localOrShuffleGrouping("distribute", CommonConstant.JVM_THREAD);
        topologyBuilder.setBolt("persist-jvm-class", PersistToMySQLBoltBuilder.createJvmClassInsertBolt(), 1)
                .localOrShuffleGrouping("distribute", CommonConstant.JVM_CLASS);


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