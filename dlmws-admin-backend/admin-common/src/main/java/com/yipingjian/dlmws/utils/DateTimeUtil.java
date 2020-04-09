package com.yipingjian.dlmws.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getTimestampFormat(Long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }
}
