package com.yipingjian.dlmws.storm.service;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TomcatLogFormatServiceTest {

    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}";
    private static final String DATE_THREAD_PATTERN = "\\[\\^[*]";

    public static void main(String[] args) {
        String firstLine = "[2020-03-08 15:35:35,255] [idc-network-schedule-pool-14] ERROR SnmpTrapSender - get OID error:1.0.8802.1.1.2.1.3.3.0";
        //JedisCommands jedisCommands;
    }
}