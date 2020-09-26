package com.yipingjian.dlmws.storm.bolt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yipingjian.dlmws.storm.common.CommonConstant;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;

public class PersistToMySQLBoltBuilder {
    private static final HashMap<String, Object> JDBC_CONFIG_MAP = Maps.newHashMap(ImmutableMap.of(
            "dataSourceClassName", "com.mysql.cj.jdbc.MysqlDataSource",
            "dataSource.url", "jdbc:mysql://192.168.1.110:3306/dlmws?serverTimezone=Asia/Shanghai&allowMultiQueries=true&autoReconnect=true&useUnicode=true&failOverReadOnly=false&useSSL=false&characterEncoding=utf-8",
            "dataSource.user", "root",
            "dataSource.password", "1874Ypj!"
    ));

    private static final ConnectionProvider connectionProvider = new HikariCPConnectionProvider(JDBC_CONFIG_MAP);

    public static JdbcInsertBolt createHostCpuInsertBolt() {
        List<Column> columnSchema = Lists.newArrayList(
                new Column("host_ip", java.sql.Types.VARCHAR),
                new Column("user_cpu", java.sql.Types.FLOAT),
                new Column("sys_cpu", java.sql.Types.FLOAT),
                new Column("time", Types.TIMESTAMP));
        JdbcMapper jdbcMapper = new SimpleJdbcMapper(columnSchema);
        return new JdbcInsertBolt(connectionProvider, jdbcMapper)
                .withInsertQuery("insert into host_cpu (host_ip, user_cpu, sys_cpu, time) values(?, ?, ?, ?)")
                .withQueryTimeoutSecs(5);
    }

    public static JdbcInsertBolt createHostMemInsertBolt() {
        List<Column> columnSchema = Lists.newArrayList(
                new Column("host_ip", java.sql.Types.VARCHAR),
                new Column("mem_used", Types.DOUBLE),
                new Column("mem_used_rate", java.sql.Types.DOUBLE),
                new Column("swap_used", Types.DOUBLE),
                new Column("swap_used_rate", java.sql.Types.DOUBLE),
                new Column("time", Types.TIMESTAMP));
        JdbcMapper jdbcMapper = new SimpleJdbcMapper(columnSchema);
        return new JdbcInsertBolt(connectionProvider, jdbcMapper)
                .withInsertQuery("insert into host_mem (host_ip, mem_used, mem_used_rate, swap_used, swap_used_rate, time) values(?, ?, ?, ?, ?, ?)")
                .withQueryTimeoutSecs(5);
    }

    public static JdbcInsertBolt createJvmThreadInsertBolt() {
        List<Column> columnSchema = Lists.newArrayList(
                new Column("host_ip", java.sql.Types.VARCHAR),
                new Column("pid", Types.INTEGER),
                new Column("total", Types.INTEGER),
                new Column("runnable", Types.INTEGER),
                new Column("time_waiting", Types.INTEGER),
                new Column("waiting", Types.INTEGER),
                new Column("time", Types.TIMESTAMP));
        JdbcMapper jdbcMapper = new SimpleJdbcMapper(columnSchema);
        return new JdbcInsertBolt(connectionProvider, jdbcMapper)
                .withInsertQuery("insert into jvm_thread (host_ip, pid, total, runnable, time_waiting, waiting, time) values(?, ?, ?, ?, ?, ?, ?)")
                .withQueryTimeoutSecs(5);
    }

    public static JdbcInsertBolt createJvmClassInsertBolt() {
        List<Column> columnSchema = Lists.newArrayList(
                new Column("host_ip", java.sql.Types.VARCHAR),
                new Column("pid", Types.INTEGER),
                new Column("class_loaded", Types.INTEGER),
                new Column("class_compiled", Types.INTEGER),
                new Column("time", Types.TIMESTAMP));
        JdbcMapper jdbcMapper = new SimpleJdbcMapper(columnSchema);
        return new JdbcInsertBolt(connectionProvider, jdbcMapper)
                .withInsertQuery("insert into jvm_class (host_ip, pid, class_loaded, class_compiled, time) values(?, ?, ?, ?, ?)")
                .withQueryTimeoutSecs(5);
    }

    public static JdbcInsertBolt createJvmMemInsertBolt() {
        List<Column> columnSchema = Lists.newArrayList(
                new Column("host_ip", java.sql.Types.VARCHAR),
                new Column("pid", Types.INTEGER),
                new Column("mem_used", Types.DOUBLE),
                new Column("mem_capacity", Types.DOUBLE),
                new Column("time", Types.TIMESTAMP));
        JdbcMapper jdbcMapper = new SimpleJdbcMapper(columnSchema);
        return new JdbcInsertBolt(connectionProvider, jdbcMapper)
                .withInsertQuery("insert into jvm_mem (host_ip, pid, mem_used, mem_capacity, time) values(?, ?, ?, ?, ?)")
                .withQueryTimeoutSecs(5);
    }
}
