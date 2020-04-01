package com.yipingjian.dlmws;

import com.yipingjian.dlmws.common.utils.ExecuteCmd;

import javax.xml.crypto.Data;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.stream.Collectors;

public class commonTest {
    public static void main(String[] args) {
        String s = ExecuteCmd.execute(new String[]{"jps", "-l", "-v"});
        String[] line = s != null ? s.split("\n") : new String[0];
        for (String aLine : line) {
            String[] one = aLine.split("\\s+");
            //排除sun.tools进程
            if (one[1].contains("sun.tools")){
                continue;
            }
            //格式化控制台输出
            if (!one[1].substring(0, 1).equals("-")) {
                String smallName = one[1].contains(".") ? one[1].substring(one[1].lastIndexOf(".")+1) : one[1];
                smallName = smallName.equalsIgnoreCase("jar")? one[1] : smallName;
               System.out.println(one[1]);
            } else {
                System.out.println(one[1]);
            }
        }
    }
}
