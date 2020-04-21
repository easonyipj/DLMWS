package com.yipingjian.dlmws.storm.config;

import org.apache.commons.collections.map.LRUMap;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUMapUtil {
    public static LRUMap RULE_MAP = new LRUMap();
    private static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

}
