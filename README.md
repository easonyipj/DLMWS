### 系统概要
系统基于ELK和Kafka搭建，使用Storm实现实时流计算功能，并提出了一种“二级缓存”机制，提高了报警规则的匹配效率，同时，采用MySQL、Elasticsearch和Redis作为存储系统，满足系统不同类型数据的存储要求，并使用SpringBoot和Vue搭建管理平台提供用户交互界面。
***

### 系统功能
1、采集分布式系统中各个节点上产生的日志。当下，互联网公司的业务系统基本上以微服务、分布式的方式进行部署，这要求日志监控报警系统不仅要采集单机上产生的各种日志，也要能够对分布式系统中不同节点上的日志进行采集；

2、针对日志数据和报警数据的发布订阅功能。整个日志监控报警系统由不同的子系统组成，不同的子系统处理数据的速率有所不同，因此设计了一个消息发布订阅模块，来缓冲实时产生的大量数据。

3、对日志进行实时处理。Tomcat产生的日志通常是文本块的形式，在浏览大量日志文件时，非常不利于找到有效信息，因此系统应该能对日志进行格式化，在持久化前将日志中的有效字段提取，方便日后的统计。同时，系统能够按照规则对每条日志进行判断，如果是风险日志，则应该按照用户添加的规则进行报警。

4、提供对性能数据的实时监控界面，并能检索历史日志。本系统能实时监控性能关键数据、JVM关键数据并提供日志搜索的UI界面，并且能根据关键字快速搜索出相关的日志。

5、实时触达报警消息。对符合报警规则的日志进行报警通知，可以通过钉钉或者邮件的方式进行通知，触达到用户后，用户可以选择静默该报警。

6、对监控数据和统计结果进行可视化展示。系统应该提供一个大盘界面，界面实时刷新关键统计数据，比如日志总数、报警总数、日志分等级统计、报警分日志类别统计等等。同时，对于历史的日志数据、报警数据也应该提供统计分析功能，并对统计结果进行可视化查询。
***
### 系统流程
![系统流程](https://github.com/easonyipj/DLMWS/blob/master/pics/系统流程.png)
日志被采集模块采集后，依次经过预处理、实时分析，然后并行进行实时统计、持久化、报警通知，与此同时，实时统计和报警通知的结果也会被持久化。
***
### 系统架构
![系统架构](https://github.com/easonyipj/DLMWS/blob/master/pics/架构图.png)
###### 日志收集
日志采集模块主要负责将日志从分布式系统各节点上收集，然后发送到预处理模块或者消息队列中。主要待采集的日志有两种类型，Tomcat日志和性能日志。
系统日志采集模块将由两个子模块组成，分别是Elastic公司的Filebeat和自己编写的SpringBoot Web服务Agent组成，两者均通过脚本自动下载。其中Filebeat采集主机服务上的Tomcat日志输出到Logstash进行预处理，Agent采集性能日志输出到Kafka。
![日志收集](https://github.com/easonyipj/DLMWS/blob/master/pics/采集模块.png)
###### 日志预处理
本模块将使用Elastic公司的Logstash实现，通过Logstash中的pipeline实时对日志进行预处理，一个pipeline包含Inputs、Filters和Outputs。在本系统中，Inputs为Filebeat，Filters包含一个Grok过滤器，根据设定好的正则表达式，提取出初始日志中的有效信息并以属性-值的形式附加到数据流中，然后根据特定字段输出到Kafka中。
###### 日志实时计算
本模块将采用Storm来实现实时计算功能，不同于Hadoop的离线批处理计算、Spark的实时批处理计算，Storm具有“纯实时”的特性，面向数据流，即时处理数据流中的每一条数据，延时可达毫秒级。
Storm通过运行用户编写的Topology来完成实时计算任务，一个Topology可由一个或多个的输入节点Spout和处理节点Bolt组成。本系统中，Spout节点负责从Kafka中获取日志数据、Bolt节点负责对日志进行最终格式化、报警匹配、计数统计和持久化，本系统中的Topology示意图如下图所示。
![实时计算](https://github.com/easonyipj/DLMWS/blob/master/pics/topo示意图.png)

Spout从Kafka中读取日志信息后，LogFormatBolt按照日志类型对日志进行不同的格式化，然后系统按照持久化、报警和统计分别并行处理：
1、DistributeBolt接收LogFormatBolt传输的格式化后的日志流，按照日志的类型分别将日志持久化到MySQL和Elasticsearch中，其中HostMem（主机内存使用率）、HostCpu（主机CPU使用率）、JvmMem（JVM内存使用率）、JvmThread（JVM线程数量分布）和JvmClass（JVM类加载情况）将持久化到MySQL中，TomcatLog（Tomcat日志）将持久化到Elasticsearch。
2、WarningBolt接收LogFormatBolt传输的格式化后的日志流，对日志进行初步筛选，筛选出需要进行进一步报警匹配和报警计数的日志，发送到EWMABolt和IntervalBolt进行指数加权移动平均值计算和时间序列计算。
3、CountBolt接收LogFormatBolt、WarningBolt、EWMABolt和IntervalBolt的计数关键字，进行累计操作。
上述三条任务流按照功能解耦，实现了报警、持久化和统计计数的并行处理。
WarningBolt负责对日志进行报警预处理，根据报警规则，将直接进行报警或者对日志进行关键字计数或者指数移动平均值计算后根据计算结果报警。为了实现报警规则的高效匹配，通过对其他文献的调研和自己的思考，本系统将采用一个“二级缓存”机制来存储报警规则。
一级缓存位于Storm内存，其形式是一个实现LRU算法的Map，key为项目名，value为项目规则列表。LRU指最近最少使用算法，可以使热点数据保存在系统内存中。Apache的开源工具包提供了可以实现LRU算法的LRUMap，LRUMap中保存的是日志产生频率最高的项目报警规则，避免从外部系统中读取配置信息。
二级缓存采用Redis，以key-value的形式存放所有项目的报警规则，当LRU 
Map中不存在项目的报警规则时，从Redis中读取报警配置，并按照“最近最少使用”的淘汰策略更新LRU Map。Redis中存储报警规则的key为“项目名-日志类型”的格式，例如“dlmws-log:tomcat“。
###### 消息服务
日志预处理模块和Agent作为生产者，向Kafka的tomcat、host-mem、host-cpu、jvm-mem、jvm-thread、jvm-class主题中生产消息，Storm作为消费者，消费以上主题中的日志数据并进行实时处理，然后将符合报警规则的日志加工为报警消息，作为生产者向Kafka的warning主题中发送报警消息，报警服务作为消费者，消费warning主题中的报警信息，根据解析后的内容进行消息发送。
###### 报警服务
报警服务是一个采用SpringBoot开发的Web服务，其负责消费并解析Kafka中warning主题的报警信息，根据解析后的信息，组装报警内容，发送报警邮件或钉钉消息。
![报警服务](https://github.com/easonyipj/DLMWS/blob/master/pics/钉钉报警.png)

###### 存储服务
存储服务是系统的基础服务，主要提供对系统中各类型数据的持久化服务。由于系统中各种类型数据非常多，不同类型的数据的操作也不尽相同，因此系统采用MySQL、Elasticsearch和Redis作为存储服务，持久化不同类型的数据。
MySQL主要存储系统配置信息，用户信息，用户的项目、主机和Java实例信息，性能日志信息等实时读取性要求较低，不要求复杂关键字查询的信息。同时，MySQL也作为Redis的备份，持久化Redis中部分信息的副本。
Elasticsearch主要存储Tomcat日志信息，Tomcat日志要求能够根据关键字进行灵活的查询，并进行复杂的统计聚合分析，因此采用Elasticsearch作为存储服务。
Redis中存放的是报警规则信息和实时统计信息，这些数据要求能进行快速地存取，因此放在内存型数据库中。
###### 管理平台
管理平台是用户和系统交互的平台，用户使用管理平台完成数据大盘监控、添加项目、添加主机、监控性能数据、监控服务日志、检索服务日志、查看统计分析结果、编辑报警规则、查看报警记录、查看报警统计等。
![数据大盘](https://github.com/easonyipj/DLMWS/blob/master/pics/数据大盘.png)
数据大盘
![主机监控](https://github.com/easonyipj/DLMWS/blob/master/pics/主机监控.png)
主机监控
![服务日志监控](https://github.com/easonyipj/DLMWS/blob/master/pics/服务日志监控.png)
服务日志监控
![日志检索](https://github.com/easonyipj/DLMWS/blob/master/pics/日志检索.png)
日志检索
![Java进程监控](https://github.com/easonyipj/DLMWS/blob/master/pics/Java进程监控.png)
Java进程监控
![报警监控](https://github.com/easonyipj/DLMWS/blob/master/pics/报警监控.png)
报警监控
![报警规则](https://github.com/easonyipj/DLMWS/blob/master/pics/报警规则.png)
报警规则










