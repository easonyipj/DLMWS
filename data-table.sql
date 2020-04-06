DROP TABLE sys_user;;/*SkipError*/
CREATE TABLE sys_user(
    id INT NOT NULL AUTO_INCREMENT  COMMENT '用户id' ,
    username VARCHAR(32) NOT NULL   COMMENT '用户名' ,
    password VARCHAR(32) NOT NULL   COMMENT '密码' ,
    salt VARCHAR(32) NOT NULL   COMMENT 'salt' ,
    role VARCHAR(32) NOT NULL   COMMENT '角色' ,
    create_time DATETIME NOT NULL   COMMENT '创建时间' ,
    PRIMARY KEY (id)
) COMMENT = '用户表 用户信息';;

ALTER TABLE sys_user ADD UNIQUE id(id);;
ALTER TABLE sys_user COMMENT '用户表';;
DROP TABLE project;;/*SkipError*/
CREATE TABLE project(
    id INT NOT NULL AUTO_INCREMENT  COMMENT '项目id' ,
    user_id INT NOT NULL   COMMENT '用户id' ,
    name VARCHAR(32)    COMMENT '项目名' ,
    description VARCHAR(128)    COMMENT '项目描述' ,
    status INT    COMMENT '项目状态' ,
    create_time DATETIME    COMMENT '创建时间' ,
    PRIMARY KEY (id)
) COMMENT = '项目表 项目信息';;

ALTER TABLE project COMMENT '项目表';;
DROP TABLE host;;/*SkipError*/
CREATE TABLE host(
    id INT NOT NULL AUTO_INCREMENT  COMMENT '主机id' ,
    ip VARCHAR(32) NOT NULL   COMMENT '主机ip' ,
    name VARCHAR(32) NOT NULL   COMMENT '主机名' ,
    project_id VARCHAR(32) NOT NULL   COMMENT '关联项目id' ,
    username VARCHAR(32) NOT NULL   COMMENT '关联用户名' ,
    info TEXT NOT NULL   COMMENT '主机信息' ,
    status VARCHAR(1) NOT NULL   COMMENT '主机状态' ,
    PRIMARY KEY (id)
) COMMENT = '主机表 保存主机信息';;

ALTER TABLE host COMMENT '主机表';;
DROP TABLE jvm_thread;;/*SkipError*/
CREATE TABLE jvm_thread(
    id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
    host_ip VARCHAR(32) NOT NULL   COMMENT '主机ip' ,
    pid INT NOT NULL   COMMENT 'pid' ,
    total INT NOT NULL   COMMENT '线程总数' ,
    runnable INT    COMMENT '运行线程数' ,
    time_waiting INT    COMMENT '等待线程数' ,
    wating INT    COMMENT '超时等待线程数' ,
    time DATETIME NOT NULL   COMMENT '时间' ,
    PRIMARY KEY (id)
) COMMENT = 'JVM线程数据表 JVM线程数据表';;

ALTER TABLE jvm_thread COMMENT 'JVM线程数据表';;
DROP TABLE jvm_memory;;/*SkipError*/
CREATE TABLE jvm_memory(
    id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
    host_ip VARCHAR(32) NOT NULL   COMMENT '主机ip' ,
    pid INT NOT NULL   COMMENT 'pid' ,
    mem_used INT NOT NULL   COMMENT '堆内存使用大小M' ,
    mem_capacity INT    COMMENT '堆内存容量M' ,
    time DATETIME NOT NULL   COMMENT '时间' ,
    PRIMARY KEY (id)
) COMMENT = 'JVM堆内存数据表 JVM堆内存数据表';;

ALTER TABLE jvm_memory COMMENT 'JVM堆内存数据表';;
DROP TABLE jvm_class;;/*SkipError*/
CREATE TABLE jvm_class(
    id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
    pid INT NOT NULL   COMMENT 'pid' ,
    host_ip VARCHAR(32) NOT NULL   COMMENT '主机ip' ,
    class_loaded INT NOT NULL   COMMENT '类加载数量' ,
    class_compiled INT    COMMENT '类编译数量' ,
    time DATETIME NOT NULL   COMMENT '时间' ,
    PRIMARY KEY (id)
) COMMENT = 'JVM类加载数据表 JVM类加载数据表';;

ALTER TABLE jvm_class COMMENT 'JVM类加载数据表';;
DROP TABLE host_cpu;;/*SkipError*/
CREATE TABLE host_cpu(
    id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
    host_ip VARCHAR(32)    COMMENT '主机ip' ,
    user_cpu DECIMAL(32,10)    COMMENT '用户cpu使用率' ,
    sys_cpu DECIMAL(32,10)    COMMENT '系统cpu使用率' ,
    time DATETIME    COMMENT '时间' ,
    PRIMARY KEY (id)
) COMMENT = '主机CPU信息 主机CPU信息';;

ALTER TABLE host_cpu COMMENT '主机CPU信息';;
DROP TABLE host_memory;;/*SkipError*/
CREATE TABLE host_memory(
    id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
    host_ip VARCHAR(32)    COMMENT '主机ip' ,
    me_used DECIMAL(32,10)    COMMENT '内存使用量M' ,
    mem_used_rate DECIMAL(32,10)    COMMENT '内存使用率' ,
    swap_used VARCHAR(32)    COMMENT '交换区使用量M' ,
    swap_used_rate DECIMAL(32,10)    COMMENT '交换区使用率' ,
    time DATETIME    COMMENT '时间' ,
    PRIMARY KEY (id)
) COMMENT = '主机内存信息 主机内存信息';;

ALTER TABLE host_memory COMMENT '主机内存信息';;
DROP TABLE host_disk;;/*SkipError*/
CREATE TABLE host_disk(
    id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
    host_ip VARCHAR(32)    COMMENT '主机ip' ,
    disk_used DECIMAL(32,10)    COMMENT '磁盘使用率' ,
    time DATETIME    COMMENT '时间' ,
    PRIMARY KEY (id)
) COMMENT = '主机磁盘信息 主机磁盘信息';;

ALTER TABLE host_disk COMMENT '主机磁盘信息';;
