package com.yipingjian.dlmws.storm.topology;

import com.yipingjian.dlmws.storm.bolt.*;
import com.yipingjian.dlmws.storm.common.CommonConstant;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.kafka.spout.FirstPollOffsetStrategy;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import java.util.Properties;

public class DLMWSTopology {

    public static void main(String[] args) throws Exception {

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        Properties properties = new Properties();
        // kafka server的基本配置
        properties.setProperty("group.id", "test-news-topic");
        // 定义一个KafkaSpoutConfig
        KafkaSpoutConfig<String, String> kafkaSpoutConfig = KafkaSpoutConfig.builder("192.168.0.19:9092",
                "tomcat", "host-cpu", "host-mem", "jvm-mem", "jvm-thread", "jvm-class")
                .setFirstPollOffsetStrategy(FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST)
                .setProp(properties).build();
        // 定义KafkaBolt
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.0.19:9092");
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        @SuppressWarnings("unchecked")
        KafkaBolt bolt = new KafkaBolt()
                .withProducerProperties(props)
                .withTopicSelector(new DefaultTopicSelector("warning"))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());



        // KafkaSpout 实例
        KafkaSpout<String, String> kafkaSpout = new KafkaSpout<>(kafkaSpoutConfig);
        // 注入Spout
        topologyBuilder.setSpout("kafka-spout", kafkaSpout, 1);
        // 获取kafka-spout数据 进行格式化
        topologyBuilder.setBolt("log-format", new LogFormatBolt(), 4).shuffleGrouping("kafka-spout");
        // 根据配置对数据告警
        topologyBuilder.setBolt("warning-format", new WarningBolt(), 2).shuffleGrouping("log-format");
        // 时间序列阈值计算
        topologyBuilder.setBolt("interval-bolt", new IntervalCountBolt(), 2)
                .fieldsGrouping("warning-format", CommonConstant.INTERVAL_TYPE, new Fields("keyword"));
        // 指数移动平均值计算
        topologyBuilder.setBolt("ewma-bolt", new EWMABolt(), 2)
                .fieldsGrouping("warning-format", CommonConstant.EWMA_TYPE, new Fields("ip"));
        // 发送到警报消息队列
        topologyBuilder.setBolt("kafka-bolt", bolt, 2)
                .localOrShuffleGrouping("warning-format", CommonConstant.IMMEDIATE_TYPE)
                .shuffleGrouping("interval-bolt")
                .shuffleGrouping("ewma-bolt");


        // 持久化分发bolt
//        topologyBuilder.setBolt("distribute", new DistributeBolt(), 2).shuffleGrouping("log-format");
//        // 持久化tomcat log到es集群
//        topologyBuilder.setBolt("persist-tomcat-log", new PersistTomcatLogBolt(), 1)
//                .localOrShuffleGrouping("distribute", CommonConstant.TOMCAT);
//        // 持久化host log到 mysql
//        topologyBuilder.setBolt("persist-host-mem", PersistToMySQLBoltBuilder.createHostMemInsertBolt(), 1)
//                .localOrShuffleGrouping("distribute", CommonConstant.HOST_MEM);
//        topologyBuilder.setBolt("persist-host-cpu", PersistToMySQLBoltBuilder.createHostCpuInsertBolt(), 1)
//                .localOrShuffleGrouping("distribute", CommonConstant.HOST_CPU);
//        // 持久化jvm log到mysql
//        topologyBuilder.setBolt("persist-jvm-mem", PersistToMySQLBoltBuilder.createJvmMemInsertBolt(), 1)
//                .localOrShuffleGrouping("distribute", CommonConstant.JVM_MEM);
//        topologyBuilder.setBolt("persist-jvm-thread", PersistToMySQLBoltBuilder.createJvmThreadInsertBolt(), 1)
//                .localOrShuffleGrouping("distribute", CommonConstant.JVM_THREAD);
//        topologyBuilder.setBolt("persist-jvm-class", PersistToMySQLBoltBuilder.createJvmClassInsertBolt(), 1)
//                .localOrShuffleGrouping("distribute", CommonConstant.JVM_CLASS);


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
